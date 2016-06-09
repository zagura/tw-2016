-module(cw2_2_2).

-export([produce/2, consume/2, main/3, buffer/5, submain/3]).


produce(Buffer, ID) -> 
	Buffer ! {produce, ID, self()},
	receive
		{accept, Pid} -> timer:sleep(50),
			produce(Pid, ID);
		{reject, Pid} -> produce(Pid, ID)
	end.

consume(Buffer, ID) ->
	Buffer ! {consume, ID, self()},
	receive
		{accept, Pid} -> timer:sleep(50),
			consume(Pid, ID);
		{reject, Pid} -> consume(Pid, ID)
	end.

%% Initializing end buffer
buffer(0, Buff_ID, nil, C_token, P_token) ->
	receive
		{register, Pid} -> buffer(1, Buff_ID, Pid, C_token, P_token)
	end;

%% empty buffer
buffer(0, Buff_ID, Next, C_token, true) ->
	receive
		{produce, ID, Pid} -> 
			io:format("Producer ~p at index: ~p ~n", [ID, Buff_ID]),
			Pid ! {accept, Next},
			Next ! {give_token, produce},
			buffer(1, Buff_ID, Next, C_token, false);
		{consume, _ID, Pid} -> 
			Pid ! {reject, Next},
			buffer(0, Buff_ID, Next, C_token, true)
	end;
	

%% full buffer
buffer(1, Buff_ID, Next, true, P_token) ->
	receive
		{consume, ID, Pid} -> 
			io:format("Consumer ~p at index: ~p ~n", [ID, Buff_ID]),
			Pid ! {accept, Next},
			Next ! {give_token, consume},
			buffer(0, Buff_ID, Next, false , P_token);
		{produce, _ID, Pid} ->
			Pid ! {reject, Next},
			buffer(1, Buff_ID, Next, true, P_token)
	end;

buffer(State, Buff_ID, Next, C_token, false) ->
	receive
		{produce, _ID, Pid} ->
			Pid ! {reject, Next},
			buffer(State, Buff_ID, Next, C_token, false);
		{consume, _ID, Pid} ->
			Pid ! {reject, Next},
			buffer(State, Buff_ID, Next, C_token, false);
		{give_token, consume} ->
			buffer(State, Buff_ID, Next, true, false);
		{give_token, produce} -> 
			buffer(State, Buff_ID, Next, C_token, true)
	end;


buffer(State, Buff_ID, Next, false, P_token) ->
	receive
		{produce, _ID, Pid} ->
			Pid ! {reject, Next},
			buffer(State, Buff_ID, Next, false, P_token);
		{consume, _ID, Pid} ->
			Pid ! {reject, Next},
			buffer(State, Buff_ID, Next, false, P_token);
		{give_token, consume} ->
			buffer(State, Buff_ID, Next, true, P_token);
		{give_token, produce} -> 
			buffer(State, Buff_ID, Next, false, true)
	end.
 


% buffer(Size, Counter, Begin, End, Buff_ID) -> 
% 	receive
% 		{consume, ID, Pid} -> 
% 			io:format("Consumer ~p at index ~p ~n", [ID, Begin]),
% 			Pid ! {accept, consume},
% 			buffer(Size, Counter-1, (Begin+1) rem Size, End, Buff_ID);

% 		{produce, ID, Pid} ->
% 			io:format("Producer ~p at index ~p ~n", [ID, End]),
% 			Pid ! {accept, produce},
% 			buffer(Size, Counter+1, Begin, (End+1) rem Size, Buff_ID)
% 	end.

submain(_Name ,0, _Pid) -> nil;

submain(consumer, Number, Pid) ->
	spawn_link(?MODULE, consume, [Pid, Number]),
	submain(consumer, Number - 1, Pid);

submain(producer, Number, Pid) ->
	spawn_link(?MODULE, produce, [Pid, Number]),
	submain(producer, Number - 1, Pid).


buffer_init(0, Pid0) ->
	Pid0;
buffer_init(N, Pid0) ->
	Pid = spawn_link(?MODULE, buffer, [0, N, Pid0, false, false]),
	buffer_init(N-1, Pid).


main(Prods, Cons, Size) ->
	Pid0 = spawn_link(?MODULE, buffer, [0, Size, nil, true, true]),
	Pid = buffer_init(Size - 1, Pid0),
	Pid0 ! {register, Pid},
	submain(consumer, Cons, Pid),
	submain(producer, Prods, Pid).
