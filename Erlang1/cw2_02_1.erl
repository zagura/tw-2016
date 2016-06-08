-module(cw2).

-export([produce/2, consume/2, main/3, buffer/4]).


produce(Buffer, ID) -> 
	Buffer ! {produce, ID, self()},
	receive
		{accept, produce} -> timer:sleep(10),
			produce(Buffer, ID)
	end.

consume(Buffer, ID) ->
	Buffer ! {consume, ID, self()},
	receive
		{accept, consume} -> timer:sleep(10),
			consume(Buffer, ID)
	end.

%% empty buffer
buffer(0, Buf_ID, Next) ->
	receive
		{produce, ID, Pid} -> 
			io:format("Producer ~p at index: ~p ~n", [ID, Begin]),
			Pid ! {accept, produce}
	end,
	buffer(Size, 1, Begin, (Begin+1) rem Size);

%% full buffer
buffer(1, Buf_ID, Next) ->
	receive
		{consume, ID, Pid} -> 
			io:format("Consumer ~p at index: ~p ~n", [ID, Begin]),
			Pid ! {accept, consume}
		{produce, ID, Pid}
	end,
	buffer(Size, Size-1, (Begin+1) rem Size, End);


buffer(Size, Counter, Begin, End, Buf_ID) -> 
	receive
		{consume, ID, Pid} -> 
			io:format("Consumer ~p at index ~p ~n", [ID, Begin]),
			Pid ! {accept, consume},
			buffer(Size, Counter-1, (Begin+1) rem Size, End, Buf_ID);

		{produce, ID, Pid} ->
			io:format("Producer ~p at index ~p ~n", [ID, End]),
			Pid ! {accept, produce},
			buffer(Size, Counter+1, Begin, (End+1) rem Size, Buf_ID)
	end.

submain(_Name ,0, _Pid) -> nil;

submain(consumer, Number, Pid) ->
	spawn_link(?MODULE, consume, [Pid, Number]),
	submain(consumer, Number - 1, Pid);

submain(producer, Number, Pid) ->
	spawn_link(?MODULE, produce, [Pid, Number]),
	submain(producer, Number - 1, Pid).

buffer_init(N) ->


main(Prods, Cons, Size) ->
	Pid = buffer_init(Size),
	submain(consumer, Cons, Pid),
	submain(producer, Prods, Pid).
