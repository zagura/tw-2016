-module(cw1).

-export([send/3, send2/4, procesA/2, procesB/2, procesC/3, procesA2/2, procesB2/2, main/2]).

send(_Msg, _Pid, 0) -> io:format("send end");
send(Msg, Pid, Num) ->
	Pid ! Msg,
	send(Msg, Pid, Num-1).

send2(_Msg, _Pid, 0, _Iter) -> io:format("send end");
send2(Msg, Pid, Num, Iter) ->
	Pid ! {Msg, Iter},
	send2(Msg, Pid, Num-1, Iter+1).




procesA(PidC, Num) ->
	send(aaa, PidC, Num).

procesB(PidC, Num) ->
	send(bbb, PidC, Num).

procesA2(PidC, Num) ->
	io:format("A2"),
	send2(aaa, PidC, Num, 0).

procesB2(PidC, Num) ->
	send2(bbb, PidC, Num, 0).

procesC(_Ver, _Tmp, 0) -> nil;
procesC(ver1, aaa, Num) ->
	receive
		aaa -> io:format("C1 : aaa~n")
	end,
	receive
		bbb -> io:format("C1 : bbb~n")
	end,
	procesC(ver1, aaa, Num-1);

% procesC(ver1, bbb) ->
% 	receive
% 		bbb -> io:format("C1 : bbb~n")
% 	end,
% 	procesC(ver1, aaa);

procesC(ver2, c2_1, Num) ->
	receive
		aaa -> io:format("C2 01 : aaa~n");
		bbb -> io:format("C2 01 : bbb~n")
	end,
	procesC(ver2, c2_1, Num -1 );

% procesC(ver22, {IterA, IterB}, Num) ->
% 	receive
% 		{aaa, Iter} -> io:format("C2 02 : aaa - ~p ~p~n", [Iter, IterA]),
% 				NewIter = {IterA + 1, IterB};
% 		{bbb, Iter} -> io:format("C2 02 : bbb - ~p ~p ~n", [Iter, IterB]),
% 				NewIter = {IterA, IterB+1};
% 		Msg -> io:format("~w", [Msg])
% 	end,
% 	procesC(ver22, NewIter, Num-1);

procesC(ver2_2, _Iter, Num) ->
	receive
		{aaa, Iter} -> io:format("C2 02 : aaa - ~p~n", [Iter]);
				% NewIter = {IterA + 1, IterB};
		{bbb, Iter} -> io:format("C2 02 : bbb - ~p ~n", [Iter]);
				% NewIter = {IterA, IterB+1};
		Msg -> io:format("~w", [Msg])
	end,
	procesC(ver2_2, {0,0}, Num-1);


procesC(ver3, ver3, Num) ->
	receive
		Msg -> io:format("~w ~n", [Msg])
	end,
	procesC(ver3, ver3, Num -1).


main(ver1, Num) ->
	PidC = spawn_link(?MODULE, procesC, [ver1, aaa, 2*Num]),
	%io:format("PidC: ~d", PidC),
	PidA = spawn_link(?MODULE, procesA, [PidC, Num]),
	%io:format("PidC: ~d", PidC),
	PidB = spawn_link(?MODULE, procesB, [PidC, Num]);
	%io:format("PidC: ~d", PidC),
main(ver2, Num) ->
	PidC = spawn_link(?MODULE, procesC, [ver2, c2_1, 2*Num]),
	%io:format("PidC: ~d", PidC),
	PidA = spawn_link(?MODULE, procesA, [PidC, Num]),
	%io:format("PidC: ~d", PidC),
	PidB = spawn_link(?MODULE, procesB, [PidC, Num]);
	%io:format("PidC: ~d", PidC),
main(ver2_2, Num) ->
	PidC = spawn_link(?MODULE, procesC, [ver2_2, none, 2*Num]),
	%io:format("PidC: ~d", PidC),
	PidA = spawn_link(?MODULE, procesA2, [PidC, Num]),
	%io:format("PidC: ~d", PidC),
	PidB = spawn_link(?MODULE, procesB2, [PidC, Num]);
	%io:format("PidC: ~d", PidC),
main(ver3, Num) ->
	PidC = spawn_link(?MODULE, procesC, [ver3, ver3, 3*Num]),
	%io:format("PidC: ~d", PidC),
	PidA = spawn_link(?MODULE, procesA, [PidC, Num]),
	%io:format("PidC: ~d", PidC),
	PidB = spawn_link(?MODULE, procesB, [PidC, Num]),
	%io:format("PidC: ~d", PidC),
	PidC ! hello.

	% receive
	% 	aaa -> aaa
	% end,
	% receive
	% 	bbb -> ccc
	% end.
	% loop();