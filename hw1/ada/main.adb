with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;

procedure Main is

   Upper_Bound     : constant Positive := 100;
   Max_Threads     : constant Positive := 50;
   First_Thread_Id : constant Positive := 2;

   task type Thread_Task is
      entry Start (Id     : in Positive);
      entry Put   (Number : in Natural);
   end Thread_Task;

   type Thread_Task_Access is access Thread_Task;

   type Thread_Task_Arr is array (Positive range <>) of Thread_Task_Access;

   protected type Task_Manager is
      procedure Get_Next_Thread
        (Current_Id : in Positive; Next : out Thread_Task_Access;
         Should_Start : out Boolean; Start_Id : out Positive);
   private
      Filters : Thread_Task_Arr(First_Thread_Id .. Max_Threads) := (others => null);
   end Task_Manager;

   Manager : Task_Manager;

   protected body Task_Manager is
      procedure Get_Next_Thread
        (Current_Id : in Positive; Next : out Thread_Task_Access;
         Should_Start : out Boolean; Start_Id : out Positive)
      is
         Next_Id : constant Positive := Current_Id + 1;
      begin
         if Next_Id > Max_Threads then
            Next := null;
            Should_Start := False;
            Start_Id := 1;
            return;
         end if;

         if Filters(Next_Id) = null then
            Filters(Next_Id) := new Thread_Task;
            Should_Start := True;
            Start_Id := Next_Id;
         else
            Should_Start := False;
            Start_Id := 1;
         end if;

         Next := Filters(Next_Id);
      end Get_Next_Thread;
   end Task_Manager;

   task body Thread_Task is
      My_Id         : Positive;
      Next_Thread   : Thread_Task_Access;
      Should_Stop   : Boolean := False;
   begin
      accept Start (Id : in Positive) do
         My_Id := Id;
      end Start;

      loop
         exit when Should_Stop;

         accept Put (Number : in Natural) do
            if Number = 0 then
               if Next_Thread /= null then
                  Next_Thread.Put(0);
                  -- Put_Line("Thread" & My_Id'Image & " relaying shutdown signal.");
               end if;
               Should_Stop := True;
            elsif Number mod My_Id /= 0 then
               if Next_Thread = null then
                  declare
                     Should_Start : Boolean;
                     Start_Id : Positive;
                  begin
                     Manager.Get_Next_Thread(Number, Next_Thread, Should_Start, Start_Id);
                     if Should_Start then
                        Put_Line (Natural'Image(Number));
                        Next_Thread.Start(Number);
                     end if;
                  end;
               end if;

               if Next_Thread /= null then
                  Next_Thread.Put(Number);
               else
                  -- Put_Line("Thread" & My_Id'Image & " passed " & Natural'Image(Number) & " to end of pipe (prime).");
                  Put_Line("");
               end if;
            else
               -- Put_Line("Thread" & My_Id'Image & " consumed: " & Natural'Image(Number));
               Put_Line("");
            end if;
         end Put;
      end loop;

      -- Put_Line("Thread" & My_Id'Image & " shutting down.");
   end Thread_Task;

   First_Thread : Thread_Task_Access;

begin
   Put_Line("--- START ---");
   -- Put_Line("Feeding numbers from 2 to" & Positive'Image(Upper_Bound));

   declare
      Should_Start : Boolean;
      Start_Id : Positive;
   begin
      Manager.Get_Next_Thread(Current_Id   => First_Thread_Id - 1,
                              Next         => First_Thread,
                              Should_Start => Should_Start,
                              Start_Id     => Start_Id);
      if Should_Start then
         -- Put_Line ("Spawning thread for ID: " & Positive'Image(Start_Id));
         First_Thread.Start(Start_Id);
      end if;
   end;

   for I in 2 .. Upper_Bound loop
      First_Thread.Put(I);
   end loop;

   Put_Line("--- SENDING SHUTDOWN SIG ---");
   First_Thread.Put(0);

   Put_Line("--- FIN ---");
end Main;
