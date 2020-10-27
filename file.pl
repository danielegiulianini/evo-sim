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

%Boundary collision fatto nella newPosition.
standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(FinalAngle, NewStep)):- Step =:= 0, rand_int(360, NewAngle), rand_int(50, NewStep), newPosition(Point, Velocity, NewAngle, PI, FinalAngle, NewPoint), !.
standardMov(Point, Velocity, Angle, Step, PI, NewPoint, direction(FinalAngle, NewStep)):- NewStep is Step-1, newPosition(Point, Velocity, Angle, PI, FinalAngle, NewPoint).

chaseMov(point(X, Y), point(ChasedX, ChasedY), Velocity, PI, NewPoint, direction(FinalAngle, 0)):- DiffX is ChasedX - X, DiffY is ChasedY - Y, atan2(DiffY, DiffX, PI, Angle), toDegree(Angle, PI, Degrees), newPosition(point(X, Y), Velocity, Degrees, PI, NewAngle, NewPoint), FinalAngle is round(NewAngle).

newPosition(point(X,Y), Velocity, Degrees, PI, Degrees, point(NewX, NewY)):- toRadians(Degrees, PI, Radians), NewX is round(X+Velocity*cos(Radians)*0.05), NewY is round(Y+Velocity*sin(Radians)*0.05), between(NewX, 0, 1280), between(NewY, 0, 720), !.
newPosition(point(X,Y), Velocity, Degrees, PI, FinalAngle, point(NewX, NewY)):- rand_int(360, NewAngle), newPosition(point(X,Y), Velocity, NewAngle, PI, FinalAngle, point(NewX, NewY)).

toRadians(Angle, PI, Radians):- Radians is Angle*PI/180.

toDegree(Radians, PI, Degree):- Degree is Radians*180/PI.

between(X, A, B):- X >= A, X =< B.

atan2(Y, X, PI, Radians):- X > 0, Radians is atan(Y/X), !.
atan2(Y, X, PI, Radians):- X < 0, Y >= 0, Radians is atan(Y/X)+PI, !.
atan2(Y, X, PI, Radians):- X < 0, Y < 0, Radians is atan(Y/X)-PI, !.
atan2(Y, X, PI, Radians):- X =:= 0, Y > 0, Radians is PI/2, !.
atan2(Y, X, PI, Radians):- X =:= 0, Y < 0, Radians is -PI/2, !.
atan2(0, 0, PI, 0).


