/*
    Prolog code to implement the movement of an Intelligent entity.
*/


% baseMovement creates a new [[Movement]] object containing the new position and direction based on the previous position and direction. 
% The new position will approach the closest eatable entity if this is inside the FoV (Field of View), otherwise it will follow the predetermined direction.
baseMovement(EntityPoint, movingValue(EntityVelocity, EntityAngle, EntityStep, EntityFOV), Costants, ListEntities, NewPoint, NewDirection):-
					closestPoint(EntityPoint, ListEntities, ClosestEntity), 
					distance(EntityPoint, ClosestEntity, Dist), 
					Dist < EntityFOV, 
					chaseMov(EntityPoint, ClosestEntity, EntityVelocity, Costants, NewPoint, NewDirection), !. 
		
baseMovement(EntityPoint, movingValue(EntityVelocity, EntityAngle, EntityStep, EntityFOV), Costants, ListEntities, NewPoint, NewDirection):- 
					standardMov(EntityPoint, EntityVelocity, EntityAngle, EntityStep, Costants, NewPoint, NewDirection).

% closestPoint returns the closest cartesian point contained in the list from the point passed in the first term.
%closestPoint(point(0,0), [point(7,2), point(5,3), point(2,2)], X). --> X/point(2,2)
closestPoint(Point, [H|T], Min):- closestPoint(Point, T, H, Min).

closestPoint(Point, [], Min, Min).
closestPoint(Point, [H|T], Temp, Min):- distance(Point, H, Dist), distance(Point, Temp, Dist2), Dist < Dist2, !, closestPoint(Point, T, H, Min).
closestPoint(Point, [H|T], Temp, Min):- closestPoint(Point, T, Temp, Min).

% calculates the distance between two points on the 2-Dimensional Cartesian plane.
distance(point(X,Y), point(X2,Y2), Dist):- DeltaX is X2-X, DeltaY is Y2-Y, pow(DeltaX,2,PowX), pow(DeltaY,2,PowY), Dist is sqrt(PowX+PowY).

% returns the value of the first term raised to the power of the second term.
% pow(2, 3, Y). --> Y/8
pow(X, Esp, Y):-pow(X, X, Esp, Y).

pow(X, Temp, Esp, Y):- Esp=:=0, !, Y=1.
pow(X, Temp, Esp, Y):- Esp=:=1, !, Y is Temp.
pow(X, Temp, Esp, Y):- pow(X,Temp*X,Esp-1,Y).

% Calculates the new position based the previous direction if 'Step' is different from 0, otherwise a new direction is chosen and the new position will be calculated.
% It is also checked that the new position is inside the edges and if it is not the point is recalculated by changing the direction.
standardMov(Point, Velocity, Angle, Step, simulationConstants(PI, MaxDirectionStep, WorldWidth, WorldHeight, IterationPeriod), NewPoint, direction(FinalAngle, NewStep)):- Step =:= 0, rand_int(360, NewAngle), rand_int(MaxDirectionStep, NewStepValue), NewStep is NewStepValue+1, newPosition(Point, Velocity, NewAngle, PI, WorldWidth, WorldHeight, IterationPeriod, FinalAngle, NewPoint), !.
standardMov(Point, Velocity, Angle, Step, simulationConstants(PI, MaxDirectionStep, WorldWidth, WorldHeight, IterationPeriod), NewPoint, direction(FinalAngle, NewStep)):- NewStep is Step-1, newPosition(Point, Velocity, Angle, PI, WorldWidth, WorldHeight, IterationPeriod, FinalAngle, NewPoint).

% Calculates the new position based on the previous position of the blob approaching the point passed as the second term.
% StepToNextDirection is always set to 0, so when the entity stops chasing another entity it will change direction.
chaseMov(point(X, Y), point(ChasedX, ChasedY), Velocity, simulationConstants(PI, MaxDirectionStep, WorldWidth, WorldHeight, IterationPeriod), NewPoint, direction(FinalAngle, 0)):- DiffX is ChasedX - X, DiffY is ChasedY - Y, atan2(DiffY, DiffX, PI, Angle), toDegree(Angle, PI, Degrees), newPosition(point(X, Y), Velocity, Degrees, PI, WorldWidth, WorldHeight, IterationPeriod, NewAngle, NewPoint), FinalAngle is round(NewAngle).

% Calculates the new position based on direction, speed and time to perform an iteration. It is also checked that the new position is inside the edges and if it 
% is not the point is recalculated by changing the direction.
newPosition(point(X,Y), Velocity, Degrees, PI, WorldWidth, WorldHeight, IterationPeriod, Degrees, point(NewX, NewY)):- toRadians(Degrees, PI, Radians), NewX is round(X+Velocity*cos(Radians)*IterationPeriod), NewY is round(Y+Velocity*sin(Radians)*IterationPeriod), between(NewX, 0, WorldWidth), between(NewY, 0, WorldHeight), !.
newPosition(point(X,Y), Velocity, Degrees, PI, WorldWidth, WorldHeight, IterationPeriod, FinalAngle, point(NewX, NewY)):- rand_int(360, NewAngle), newPosition(point(X,Y), Velocity, NewAngle, PI, WorldWidth, WorldHeight, IterationPeriod, FinalAngle, point(NewX, NewY)).

% Conversion from degrees to radians
toRadians(Angle, PI, Radians):- Radians is Angle*PI/180.

% Conversion from degrees to radians
toDegree(Radians, PI, Degree):- Degree is Radians*180/PI.

% Check that the value of the first term is between the value of the second and third term
between(X, A, B):- X >= A, X =< B.

% Returns the angle between the ray to the point (x, y) and the positive x axis,
atan2(Y, X, PI, Radians):- X > 0, Radians is atan(Y/X), !.
atan2(Y, X, PI, Radians):- X < 0, Y >= 0, Radians is atan(Y/X)+PI, !.
atan2(Y, X, PI, Radians):- X < 0, Y < 0, Radians is atan(Y/X)-PI, !.
atan2(Y, X, PI, Radians):- X =:= 0, Y > 0, Radians is PI/2, !.
atan2(Y, X, PI, Radians):- X =:= 0, Y < 0, Radians is -PI/2, !.
atan2(0, 0, PI, 0).