-module(cw2).

-export([main/3, buffer/4, produce/3, consume/3, produce_init/3, consume_init/3]).

produce_init(Buffer, ID, N) ->
	random:seed(),
	produce(Buffer, ID, N).

produce(Buffer, ID, N) -> 
	Buffer ! {produce, ID, self(), random:uniform(N - 1) + 1},
	receive
		{accept, produce} -> timer:sleep(10),
			produce(Buffer, ID, N)
	end.

consume_init(Buffer, ID, N) ->
	random:seed(),
	consume(Buffer, ID, N).

consume(Buffer, ID, N) ->
	Buffer ! {consume, ID, self(), random:uniform(N - 1) + 1},
	receive
		{accept, consume} -> timer:sleep(10),
			consume(Buffer, ID, N)
	end.

%% empty buffer
buffer(Size, 0, Begin, Begin) ->
	receive
		{produce, ID, Pid, Portion} -> 
			io:format("Producer ~p at index: ~p ; portion size: ~p  ~n", [ID, Begin, Portion]),
			Pid ! {accept, produce},
			buffer(Size, Portion, Begin, (Begin+Portion) rem Size)
	end;

%% full buffer
buffer(Size, Size, Begin, End) ->
	receive
		{consume, ID, Pid, Portion} -> 
			io:format("Consumer ~p at index: ~p ; portion size: ~p ~n", [ID, Begin, Portion]),
			Pid ! {accept, consume},
			buffer(Size, Size-Portion, (Begin+Portion) rem Size, End)
	end;


buffer(Size, Counter, Begin, End) -> 
	receive
		{consume, ID, Pid, Portion} when Portion =< Counter -> 
			io:format("Consumer ~p at index ~p ; portion size: ~p ~n", [ID, Begin, Portion]),
			Pid ! {accept, consume},
			buffer(Size, Counter-Portion, (Begin+Portion) rem Size, End);

		{produce, ID, Pid, Portion} when Portion =< (Size - Counter) ->
			io:format("Producer ~p at index ~p ; portion size: ~p ~n", [ID, End, Portion]),
			Pid ! {accept, produce},
			buffer(Size, Counter+Portion, Begin, (End+Portion) rem Size)
	end.




main(Prods, Cons, Size) ->
	Pid = spawn_link(?MODULE, buffer, [Size*2, 0, 0, 0]),
	submain(consumer, Cons, Pid, Size),
	submain(producer, Prods, Pid, Size).


submain(_Name ,0, _Pid, _N) -> nil;

submain(consumer, Number, Pid, N) ->
	spawn_link(?MODULE, consume_init, [Pid, Number, N]),
	submain(consumer, Number - 1, Pid, N);

submain(producer, Number, Pid, N) ->
	spawn_link(?MODULE, produce_init, [Pid, Number, N]),
	submain(producer, Number - 1, Pid, N).