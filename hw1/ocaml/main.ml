open Thread

let min_int = 2
let max_int = 100
let max_threads = 50

type 'a thread_queue = {
  queue: 'a Queue.t;
  mutex: Mutex.t;
}

let create_queue () = {
  queue = Queue.create ();
  mutex = Mutex.create ();
}

let enqueue tq item =
  Mutex.lock tq.mutex;
  Queue.push item tq.queue;
  Mutex.unlock tq.mutex

let try_dequeue tq =
  Mutex.lock tq.mutex;
  let result =
    if Queue.is_empty tq.queue then None
    else Some (Queue.pop tq.queue)
  in
  Mutex.unlock tq.mutex;
  result

let thread_handles = Array.make max_threads None
let thread_queues = Array.make max_threads None
let handles_mutex = Mutex.create ()

let rec prime_thread index divisor =
  Printf.printf "%d\n" divisor;
  flush_all ();

  let my_queue = match thread_queues.(index) with
    | Some q -> q
    | None -> failwith "Queue not initialized"
  in

  let rec process_loop () =
    match try_dequeue my_queue with
    | None ->
        Thread.yield ();
        process_loop ()
    | Some num ->
        if num = -1 then (
          if index + 1 < max_threads then (
            match thread_queues.(index + 1) with
            | Some next_queue -> enqueue next_queue (-1)
            | None -> ()
          )
        ) else if num mod divisor <> 0 then (
          if index + 1 < max_threads then (
            Mutex.lock handles_mutex;
            (match thread_handles.(index + 1) with
            | None ->
                let new_queue = create_queue () in
                thread_queues.(index + 1) <- Some new_queue;
                let new_thread = Thread.create (prime_thread (index + 1)) num in
                thread_handles.(index + 1) <- Some new_thread;
                Mutex.unlock handles_mutex;
                enqueue new_queue num;
            | Some _ ->
                Mutex.unlock handles_mutex;
                match thread_queues.(index + 1) with
                | Some next_queue -> enqueue next_queue num
                | None -> failwith "Queue should exist"
            );
            process_loop ()
          )
        ) else (
          process_loop ()
        )
  in
  process_loop ()

let () =
  let first_queue = create_queue () in
  thread_queues.(0) <- Some first_queue;
  let first_thread = Thread.create (prime_thread 0) 2 in
  thread_handles.(0) <- Some first_thread;

  for i = min_int to max_int - 1 do
      enqueue first_queue i
  done;

  enqueue first_queue (-1);

  Thread.join first_thread;

  for i = 1 to max_threads - 1 do
    match thread_handles.(i) with
    | Some thread -> Thread.join thread
    | None -> ()
  done
