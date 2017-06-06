
// Rules
myName(Name)	:- .my_name(Me) & .term2string(Me, Name).
myRole(Role) 	:- myName(Name) & myRole(Name, Role).

// Personal percepts
inFacility(F) 		:- myName(Name) & inFacility(Name, F).
charge(C) 			:- myName(Name) & charge(Name, C).
load(L)				:- myName(Name) & load(Name, L).
routeLength(L)		:- myName(Name) & routeLength(Name, L).
routeDuration(D)	:- routeLength(L) & speed(S) & D = math.ceil(L / S).
lastAction(A)		:- myName(Name) & lastAction(Name, A).
lastActionResult(R) :- myName(Name) & lastActionResult(Name, R).
lastActionParam(P)  :- myName(Name) & lastActionParam(Name, P).

speed(S)		:- myRole(Role) & role(Role, S, _, _, _).
maxLoad(L)		:- myRole(Role) & role(Role, _, L, _, _).
maxCharge(C)	:- myRole(Role) & role(Role, _, _, C, _).
canUseTool(T)	:- myRole(Role) & role(Role, _, _, _, Tools) & .member(T, Tools).

chargeThreshold(X) :- maxCharge(C) & X = 0.2 * C.
capacity(C) :- maxLoad(M) & load(L) & C = M - L - 5.

// Don't know if we will need to know if they travel by road or air

// Check facility type
isChargingStation(F)	:- .substring("chargingStation", F).
isWorkshop(F)			:- .substring("workshop", F).
isStorage(F)			:- .substring("storage",  F).
isShop(F)				:- .substring("shop",     F).

// Check if agent is in this type of facility
inChargingStation 	:- inFacility(F) & isChargingStation(F).
inWorkshop 			:- inFacility(F) & isWorkshop(F).
inStorage 			:- inFacility(F) & isStorage(F).
inShop	    		:- inFacility(F) & isShop(F).
inShop(X)			:- inFacility(F) & inShop & .substring(X, F).

contains(map(Item, X), [map(Item, Y) | _]) 	:- X <= Y. 		// There is a .member function, but we need to unwrap the objects
contains(Item, [_ | Inventory]) 			:- contains(Item, Inventory). 

have(I) :- .my_name(Me) & getAgentInventory(Agent, Inv) & .member(I, Inv).

bid(Item, Bid) :- speed(S) & charge(C) & load(L) & maxLoad(M) & jia.bid(S, C, L, M, Item, Bid). // & .print(Bid).

enoughCharge :- routeLength(L) & enoughCharge(L).
enoughCharge(L) :- speed(S) & charge(C) & chargeThreshold(Threshold) & 
				Steps = math.ceil(L / S) & Steps <= (C - Threshold) / 10.
				
workshopTruck(Truck, Facility) :- truckFacility(Truck, Facility) & isWorkshop(Facility).
