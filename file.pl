%chasedMovement(point(0,0), 5, [point(7,2), point(5,3), point(4,0)], X). --> X / point(4,0)
chasedMovement(EntityPoint, EntityFOV, ListEntities, X) :- closestPoint(EntityPoint, ListEntities, ClosestEntity), distance(EntityPoint, ClosestEntity, Dist), 
																																			Dist =< EntityFOV, X=ClosestEntity.

%closestPoint(point(0,0), [point(7,2), point(5,3), point(2,2)], X). --> X/point(2,2)
closestPoint(Point, [H|T], Min):- closestPoint(Point, T, H, Min).

closestPoint(Point, [], Min, Min).
closestPoint(Point, [H|T], Temp, Min):- distance(Point, H, Dist), distance(Point, Temp, Dist2), Dist < Dist2, !, closestPoint(Point, T, H, Min).
closestPoint(Point, [H|T], Temp, Min):- closestPoint(Point, T, Temp, Min).

distance(point(X,Y), point(X2,Y2), Dist):- DeltaX is X2-X, DeltaY is Y2-Y, pow(DeltaX,2,PowX), pow(DeltaY,2,PowY), Dist is sqrt(PowX+PowY).

pow(X, Esp, Y):-pow(X, X, Esp, Y).

pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).



%standardMov(point(0,0), 1, 0, 10, 3.14, NewPoint, direction(NewAngle, NewStep)).
%standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(NewAngle, NewStep)):- Step =:= 0, rand_int(360, NewAngle), rand_int(50, NewStep), newPosition(Point, Velocity, NewAngle, PI, NewPoint), !.
%standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(Angle, NewStep)):- NewStep is Step-1, newPosition(Point, Velocity, Angle, PI, NewPoint).

%newPosition(point(X,Y), Velocity, Degrees, PI, point(NewX, NewY)):- toRadians(Degrees, PI, Radians), NewX is round(X+Velocity*cos(Radians)*0.05), NewY is round(Y+Velocity*sin(Radians)*0.05).

%delta(Velocity, , DeltaX):- DeltaX is Velocity*cos(Radians).

%toRadians(Angle, PI, Y):- Y is Angle*PI/180.


%Boundary collision fatto nella newPosition.
standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(FinalAngle, NewStep)):- Step =:= 0, rand_int(360, NewAngle), rand_int(50, NewStep), newPosition(Point, Velocity, NewAngle, PI, FinalAngle, NewPoint), !.
standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(FinalAngle, NewStep)):- NewStep is Step-1, newPosition(Point, Velocity, Angle, PI, FinalAngle, NewPoint).

newPosition(point(X,Y), Velocity, Degrees, PI, Degrees, point(NewX, NewY)):- toRadians(Degrees, PI, Radians), NewX is round(X+Velocity*cos(Radians)*0.05), NewY is round(Y+Velocity*sin(Radians)*0.05), between(NewX, 0, 1280), between(NewY, 0, 720), !.
newPosition(point(X,Y), Velocity, Degrees, PI, FinalAngle, point(NewX, NewY)):- rand_int(360, NewAngle), newPosition(point(X,Y), Velocity, NewAngle, PI, FinalAngle, point(NewX, NewY)).

toRadians(Angle, PI, Y):- Y is Angle*PI/180.

between(X, A, B):- X >= A, X =< B.

