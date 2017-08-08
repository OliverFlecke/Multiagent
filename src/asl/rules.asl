// Rules
speed(S)		:- role(_, S, _, _, _).
maxLoad(L)		:- role(_, _, L, _, _).
maxCharge(C)	:- role(_, _, _, C, _).
tools(T)		:- role(_, _, _, _, T).
canUseTool(T)	:- tools(Tools) & .member(T, Tools).
canUseAll(Req)	:- tools(Tools) & .findall(T, .member(T, Req) & .member(T, Tools), Use) &
					.length(Req, N) & .length(Use, N).
canUseAndCarry(T)		:- canUseTool(T) & canCarry([T]).
canUseAndCarry(T, Me)	:- canUseTool(T) & canCarry([T]) & .my_name(Me).
// Facility types
isChargingStation(F)	:- .substring("chargingStation", F).
isWorkshop(F)			:- .substring("workshop", F).
isStorage(F)			:- .substring("storage",  F).
isShop(F)				:- .substring("shop",     F).
isDump(F)				:- .substring("dump",     F).
// In facility
inChargingStation 	:- facility(F) & isChargingStation(F).
inWorkshop 			:- facility(F) & isWorkshop(F).
inStorage 			:- facility(F) & isStorage(F).
inShop	    		:- facility(F) & isShop(F).
inDump				:- facility(F) & isDump(F).
// Utility
routeDuration(D)	:- routeLength(L) & speed(S) & D = math.ceil(L / S).
capacity(C) 		:- maxLoad(M) & load(L) & C = M - L.
canMove 			:- charge(X) & X >= 10.
chargeThreshold(X) 	:- maxCharge(C) & X = 0.35 * C.
enoughCharge(F) 	:- getRouteLength(F, L) & speed(S) & 
						charge(C) & chargeThreshold(Threshold) & 
						Steps = math.ceil(L / S) & Steps <= (C - Threshold) / 10.
// Internal utility actions
canCarry(Items)					:- capacity(C) & jia.getBaseVolume(Items, V) & V <= C.
canSolve(Items)					:- capacity(C) & jia.getLoadReq(Items, R) & R <= C.
getBid(Items, Bid)				:- capacity(C) & speed(S) & jia.getBaseVolume(Items, V) & 
									.min([C, V], Min) & Bid = -S-Min.
// Internal actions
getInventory(Inventory)			:- .my_name(Me) & getInventory(Me, Inventory).
getInventory(Agent, Inventory) 	:- jia.getInventory(Agent, Inventory).
hasItems(Items) 				:- .my_name(Me) & hasItems(Me, Items).
hasItems(Agent, Items) 			:- jia.hasItems(Agent, Items).
hasBaseItems(Items) 			:- .my_name(Me) & hasBaseItems(Me, Items).
hasBaseItems(Agent, Items) 		:- jia.hasBaseItems(Agent, Items).
hasAmount(Item, Amount)			:- .my_name(Me) & hasAmount(Me, Item, Amount).
hasAmount(Agent, Item, Amount)	:- jia.hasAmount(Agent, Item, Amount).
hasTools(Tools)					:- .my_name(Me) & hasTools(Me, Tools).
hasTools(Agent, Tools)			:- jia.hasTools(Agent, Tools).

getRouteLength(F, L)			:- .my_name(Me) & jia.getRouteLength(Me, F, L).

contains(map(Item, X), [map(Item, Y) | _]) 	:- X <= Y.
contains(Item, [_ | Inventory]) 			:- contains(Item, Inventory). 

getUsableTools([], [], []).
getUsableTools([H|T], [H|Y], N)	:- canUseTool(H) & getUsableTools(T, Y, N).
getUsableTools([H|T], Y, [H|N]) :- getUsableTools(T, Y, N).

getInvTools(T, Y, N)			:- getInventory(I) & getInvTools(T, Y, N, I).
getInvTools([], [], [], _).
getInvTools([H|T], [H|Y], N, I)	:- contains(map(H, 1), I) & getInvTools(T, Y, N, I).
getInvTools([H|T], Y, [H|N], I)	:- getInvTools(T, Y, N, I).