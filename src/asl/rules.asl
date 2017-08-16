// Agent properties
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
chargingStation(F)	:- .substring("chargingStation", 	F).
workshop(F)			:- .substring("workshop", 			F).
storage(F)			:- .substring("storage",  			F).
shop(F)				:- .substring("shop",     			F).
// In facility
inChargingStation 	:- facility(F) & chargingStation(F).
inWorkshop 			:- facility(F) & workshop(F).
inStorage 			:- facility(F) & storage(F).
inShop	    		:- facility(F) & shop(F).

// Utility
capacity(C) 		:- maxLoad(M) & load(L) & C = M - L.
canMove 			:- charge(X) & X >= 10.
chargeThreshold(X) 	:- maxCharge(C) & X = 0.35 * C.
enoughCharge(F) 	:- charge(C) & chargeThreshold(T)
					 & getDurationToFacility(F, D) & D <= (C - T) / 10.
					 
// Internal utility actions
canCarry(Items)					:- capacity(C) & jia.items.getBaseVolume(Items, V) & V <= C.
canSolve(Items)					:- capacity(C) & jia.items.getLoadReq(Items, R) & R <= C.
getBid(Items, Bid)				:- capacity(C) & speed(S) & jia.items.getBaseVolume(Items, V) & 
									.min([C, V], Min) & Bid = -S-Min.
// Agent
getInventory(Inventory)			:- .my_name(Me) & getInventory(Me, Inventory).
getInventory(Agent, Inventory) 	:- jia.agent.getInventory(Agent, Inventory).
hasItems(Items) 				:- .my_name(Me) & hasItems(Me, Items).
hasItems(Agent, Items) 			:- jia.agent.hasItems(Agent, Items).
hasBaseItems(Items) 			:- .my_name(Me) & hasBaseItems(Me, Items).
hasBaseItems(Agent, Items) 		:- jia.agent.hasBaseItems(Agent, Items).
hasAmount(Item, Amount)			:- .my_name(Me) & hasAmount(Me, Item, Amount).
hasAmount(Agent, Item, Amount)	:- jia.agent.hasAmount(Agent, Item, Amount).
hasTools(Tools)					:- .my_name(Me) & hasTools(Me, Tools).
hasTools(Agent, Tools)			:- jia.agent.hasTools(Agent, Tools).
buyAmount(Item, Need, Buy)		:- facility(S) & getAvailableAmount(S, Item, Available) 
								 & hasAmount(Item, Has) & .min([Need - Has, Available], Buy).
// Facility
getAvailableAmount(S, I, A)		:- jia.facility.getAvailableAmount(S, I, A).
getClosestWorkshop(F, C)		:- getClosestFacility(F, "workshop", C).
getClosestFacility(F, T, C)		:- .term2string(Term, F) & jia.facility.getClosestFacility(Term, T, C).
getClosestFacility(T, C)		:- .my_name(Me) & jia.facility.getClosestFacility(Me, T, C).
getClosestShopSelling(I, S)		:- .my_name(Me) & jia.facility.getClosestShopSelling(Me, I, S).
getDurationToFacility(F, D)		:- .my_name(Me) & jia.facility.getDurationToFacility(Me, F, D).
// Items
getBaseItems	(L, B)			:- .list(L)   & jia.items.getBaseItems	(L, B).
getLoadReq		(L, R)			:- .list(L)   & jia.items.getLoadReq	(L, R).
getBaseVolume	(L, V)			:- .list(L)   & jia.items.getBaseVolume	(L, V).
getReqItems		(I, R)			:- .string(I) & jia.items.getReqItems	(I, R).
getVolume		(I, V)			:- .string(I) & jia.items.getVolume		(I, V).

contains(map(Item, X), [map(Item, Y) | _]) 	:- X <= Y.
contains(Item, [_ | Inventory]) 			:- contains(Item, Inventory). 

getUsableTools([], [], []).
getUsableTools([H|T], [H|Y], N)	:- canUseTool(H) & getUsableTools(T, Y, N).
getUsableTools([H|T], Y, [H|N]) :- getUsableTools(T, Y, N).

getInvTools(T, Y, N)			:- getInventory(I) & getInvTools(T, Y, N, I).
getInvTools([], [], [], _).
getInvTools([H|T], [H|Y], N, I)	:- contains(map(H, 1), I) & getInvTools(T, Y, N, I).
getInvTools([H|T], Y, [H|N], I)	:- getInvTools(T, Y, N, I).