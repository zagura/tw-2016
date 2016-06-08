-module(cw2).

-export([produce/2, consume/2, main/3, buffer/4]).


produce(Buffer, ID) -> 
	Buffer ! {produce, ID, self()},
	receive
		{accept, produce} -> timer:sleep(10),
			produce(Buffer, ID);
		{reject, Pid} -> produce(Pid, ID)
	end.

consume(Buffer, ID) ->
	Buffer ! {consume, ID, self()},
	receive
		{accept, consume} -> timer:sleep(10),
			consume(Buffer, ID);
		{reject, Pid} -> consume(Pid, ID)
	end.

%% Initializing end buffer
buffer(0, Buf_ID, nil) ->
	receive
		{register, Pid} -> buffer(1, Buf_ID, Pid)
	end;

%% empty buffer
buffer(0, Buf_ID, Next) ->
	receive
		{produce, ID, Pid} -> 
			io:format("Producer ~p at index: ~p ~n", [ID, Buf_ID]),
			Pid ! {accept, produce},
			buffer(1, Buf_ID, Next);
		{consume, ID, Pid} -> 
			Pid ! {reject, Next},
			buffer(0, Buf_ID, Next);
		
	end;
	

%% full buffer
buffer(1, Buf_ID, Next) ->
	receive
		{consume, ID, Pid} -> 
			io:format("Consumer ~p at index: ~p ~n", [ID, Buf_ID]),
			Pid ! {accept, consume},
			buffer(0, Buf_ID, Next);
		{produce, ID, Pid} ->
			Pid ! {reject, Next},
			buffer(1, Buf_ID, Next);
	end.
	


% buffer(Size, Counter, Begin, End, Buf_ID) -> 
% 	receive
% 		{consume, ID, Pid} -> 
% 			io:format("Consumer ~p at index ~p ~n", [ID, Begin]),
% 			Pid ! {accept, consume},
% 			buffer(Size, Counter-1, (Begin+1) rem Size, End, Buf_ID);

% 		{produce, ID, Pid} ->
% 			io:format("Producer ~p at index ~p ~n", [ID, End]),
% 			Pid ! {accept, produce},
% 			buffer(Size, Counter+1, Begin, (End+1) rem Size, Buf_ID)
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
	Pid = spawn_link(?MODULE, buffer, [0, N, Pid0]),
	buffer_init(N-1, Pid).


main(Prods, Cons, Size) ->
	Pid0 = spawn_link(?MODULE, buffer, [0, Size, nil]),
	Pid = buffer_init(Size - 1, Pid0),
	Pid0 ! {register, Pid},
	submain(consumer, Cons, Pid),
	submain(producer, Prods, Pid).
