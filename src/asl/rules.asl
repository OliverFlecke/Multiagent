// Rules
speed(S)		:- myRole(Role) & role(Role, S, _, _, _).
maxLoad(L)		:- myRole(Role) & role(Role, _, L, _, _).
maxCharge(C)	:- myRole(Role) & role(Role, _, _, C, _).
tools(T)		:- myRole(Role) & role(Role, _, _, _, T).
canUseTool(T)	:- myRole(Role) & role(Role, _, _, _, Tools) & .member(T, Tools).
// Facility types
isChargingStation(F)	:- .substring("chargingStation", F).
isWorkshop(F)			:- .substring("workshop", F).
isStorage(F)			:- .substring("storage",  F).
isShop(F)				:- .substring("shop",     F).
isDump(F)				:- .substring("dump",     F).
// In facility
inChargingStation 	:- inFacility(F) & isChargingStation(F).
inWorkshop 			:- inFacility(F) & isWorkshop(F).
inStorage 			:- inFacility(F) & isStorage(F).
inShop	    		:- inFacility(F) & isShop(F).
inShop(F)			:- inFacility(F) & inShop.
inDump				:- inFacility(F) & isDump(F).
// Utility
routeDuration(D)	:- routeLength(L) & speed(S) & D = math.ceil(L / S).
capacity(C) 		:- maxLoad(M) & load(L) & C = M - L.
canMove 			:- charge(X) & X >= 10.
chargeThreshold(X) 	:- maxCharge(C) & X = 0.35 * C.
enoughCharge 		:- routeLength(L) & enoughCharge(L).
enoughCharge(L) 	:- speed(S) & charge(C) & chargeThreshold(Threshold) & 
						Steps = math.ceil(L / S) & Steps <= (C - Threshold) / 10.
// Internal utility actions
canCarry(Items)					:- capacity(C) & jia.getBaseVolume(Items, V) & V <= C.
canSolve(Items)					:- maxLoad(L) & jia.getLoadReq(Items, R) & R <= L.
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