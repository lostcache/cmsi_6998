with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;

procedure Main is

   Upper_Bound     : constant Positive := 100;
   Max_Primes      : constant Positive := 25;

   task type Thread_Task is
      entry Start (Prime_Number : in Positive);
      entry Put   (Number : in Natural);
   end Thread_Task;

   type Thread_Task_Access is access Thread_Task;

   type Thread_List_Node;
   type Thread_List_Access is access Thread_List_Node;

   type Thread_List_Node is record
      Thread_Ref : Thread_Task_Access;
      Prime_Val  : Positive;
      Next_Node  : Thread_List_Access;
   end record;

   protected type Task_Manager is
      procedure Add_Thread (Prime_Number : in Positive; Thread_Ref : in Thread_Task_Access);
      procedure Get_Thread_For_Prime (Prime_Number : in Positive; Thread_Ref : out Thread_Task_Access; Found : out Boolean);
      procedure Get_Last_Thread (Thread_Ref : out Thread_Task_Access);
      procedure Create_New_Thread (Prime_Number : in Positive; New_Thread : out Thread_Task_Access);
   private
      Thread_List_Head : Thread_List_Access := null;
      Thread_List_Tail : Thread_List_Access := null;
   end Task_Manager;

   Manager : Task_Manager;

   protected body Task_Manager is
      procedure Add_Thread (Prime_Number : in Positive; Thread_Ref : in Thread_Task_Access) is
         New_Node : Thread_List_Access := new Thread_List_Node'(
            Thread_Ref => Thread_Ref,
            Prime_Val  => Prime_Number,
            Next_Node  => null
         );
      begin
         if Thread_List_Head = null then
            Thread_List_Head := New_Node;
            Thread_List_Tail := New_Node;
         else
            Thread_List_Tail.Next_Node := New_Node;
            Thread_List_Tail := New_Node;
         end if;
      end Add_Thread;

      procedure Get_Thread_For_Prime (Prime_Number : in Positive; Thread_Ref : out Thread_Task_Access; Found : out Boolean) is
         Current : Thread_List_Access := Thread_List_Head;
      begin
         Found := False;
         Thread_Ref := null;

         while Current /= null loop
            if Current.Prime_Val = Prime_Number then
               Thread_Ref := Current.Thread_Ref;
               Found := True;
               return;
            end if;
            Current := Current.Next_Node;
         end loop;
      end Get_Thread_For_Prime;

      procedure Get_Last_Thread (Thread_Ref : out Thread_Task_Access) is
      begin
         if Thread_List_Tail /= null then
            Thread_Ref := Thread_List_Tail.Thread_Ref;
         else
            Thread_Ref := null;
         end if;
      end Get_Last_Thread;

      procedure Create_New_Thread (Prime_Number : in Positive; New_Thread : out Thread_Task_Access) is
      begin
         New_Thread := new Thread_Task;
         Add_Thread(Prime_Number, New_Thread);
      end Create_New_Thread;
   end Task_Manager;

   task body Thread_Task is
      My_Prime      : Positive;
      Next_Thread   : Thread_Task_Access;
      Should_Stop   : Boolean := False;
   begin
      accept Start (Prime_Number : in Positive) do
         My_Prime := Prime_Number;
      end Start;

      loop
         exit when Should_Stop;

         accept Put (Number : in Natural) do
            if Number = 0 then
               if Next_Thread /= null then
                  Next_Thread.Put(0);
               end if;
               Should_Stop := True;
            elsif Number mod My_Prime /= 0 then
               if Next_Thread = null then
                  Put_Line(Natural'Image(Number));

                  Manager.Create_New_Thread(Number, Next_Thread);
                  Next_Thread.Start(Number);
               end if;

               if Next_Thread /= null then
                  Next_Thread.Put(Number);
               end if;
            end if;
         end Put;
      end loop;
   end Thread_Task;

   First_Thread : Thread_Task_Access;

begin
   Put_Line("--- START ---");

   Manager.Create_New_Thread(2, First_Thread);
   First_Thread.Start(2);
   Put_Line(" 2");

   for I in 3 .. Upper_Bound loop
      First_Thread.Put(I);
   end loop;

   Put_Line("--- SENDING SHUTDOWN SIG ---");
   First_Thread.Put(0);

   Put_Line("--- FIN ---");
end Main;
