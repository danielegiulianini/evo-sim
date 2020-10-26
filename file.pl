%point
standardMov(point(X,Y), Velocity, Angle, Step, PI, movement(point(X2,Y2), Velocity, Z, NewStep)):- Step =:= 0, rand_int(360,Z), rand_int(50,NewStep), !.
standardMov(point(X,Y), Velocity, Angle, Step, PI, movement(point(X,Y), Velocity, Angle, Step2)):- Step2 is Step-1.

%randomNumber(RangeMax,Z):- new_object('java.util.Random',[],Rand), Rand <- nextInt(RangeMax) returns Z.

lista(L, L).

newPosition(X, Y, Velocity, Degrees, PI, NewX, NewY):- toRadians(Degrees, PI, Radians), Delta is Velocity*cos(Radians).

delta(Velocity, Radians, DeltaX):- DeltaX is Velocity*cos(Radians).
%blob(point(X,Y), Velocity, FOV, Angle, Step).

toRadians(Angle, PI, Y):- Y is Angle*PI/180.