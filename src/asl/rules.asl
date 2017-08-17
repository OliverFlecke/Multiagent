// Role properties
speed(S)		:- role(_, S, _, _, _).
maxLoad(L)		:- role(_, _, L, _, _).
maxCharge(C)	:- role(_, _, _, C, _).
tools(T)		:- role(_, _, _, _, T).
capacity(C) 	:- maxLoad(M) & load(L) & C = M - L.
// Is facility type
isChargingStation(F)	:- .substring("chargingStation", 	F).
isWorkshop(F)			:- .substring("workshop", 			F).
isStorage(F)			:- .substring("storage",  			F).
isShop(F)				:- .substring("shop",     			F).
// In facility
inChargingStation 	:- facility(F) & isChargingStation(F).
inWorkshop 			:- facility(F) & isWorkshop(F).
inStorage 			:- facility(F) & isStorage(F).
inShop	    		:- facility(F) & isShop(F).
// Charge utility
canMove 			:- charge(X) & X >= 10.
chargeThreshold(X) 	:- maxCharge(C) & X = 0.35 * C.
enoughCharge(F) 	:- charge(C) & chargeThreshold(T)
					 & getDurationToFacility(F, D) & D <= (C - T) / 10.					 
// Agent
getInventory(Inventory)			:- .my_name(Me) & jia.agent.getInventory(Me, Inventory).
hasBaseItems(Items) 			:- .my_name(Me) & jia.agent.hasBaseItems(Me, Items).
hasItems	(Items) 			:- .my_name(Me) & jia.agent.hasItems(Me, Items).
hasTools	(Tools)				:- .my_name(Me) & jia.agent.hasTools(Me, Tools).
hasAmount	(Item, Amount)		:- .my_name(Me) & jia.agent.hasAmount(Me, Item, Amount).
buyAmount	(Item, Need, Buy)	:- facility(S) & getAvailableAmount(S, Item, Available) 
								 & hasAmount(Item, Has) & .min([Need - Has, Available], Buy).
// Facility
getAvailableAmount(S, I, A)		:- jia.facility.getAvailableAmount(S, I, A).
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