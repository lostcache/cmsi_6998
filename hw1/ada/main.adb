with Ada.Text_IO; use Ada.Text_IO;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;

procedure Main is

   Upper_Bound : constant Positive := 100;
   Buffer_Size : constant Positive := 10;

   type Number_Buffer is array (1 .. Buffer_Size) of Natural;


   protected type Channel is
      entry Put (Number : in Natural);
      entry Get (Number : out Natural);
      function Is_Empty return Boolean;
   private
      Buffer : Number_Buffer;
      Head   : Positive := 1;
      Tail   : Positive := 1;
      Count  : Natural := 0;
   end Channel;

   type Channel_Access is access Channel;

   protected body Channel is
      entry Put (Number : in Natural) when Count < Buffer_Size is
      begin
         Buffer(Tail) := Number;
         Tail := (Tail mod Buffer_Size) + 1;
         Count := Count + 1;
      end Put;

      entry Get (Number : out Natural) when Count > 0 is
      begin
         Number := Buffer(Head);
         Head := (Head mod Buffer_Size) + 1;
         Count := Count - 1;
      end Get;

      function Is_Empty return Boolean is
      begin
         return Count = 0;
      end Is_Empty;
   end Channel;


   task type Filter_Thread is
      entry Start (Prime_Number : in Positive; Input_Chan : in Channel_Access);
   end Filter_Thread;

   type Filter_Thread_Access is access Filter_Thread;


   protected type Thread_Manager is
      procedure Create_Next_Thread (Prime_Number : in Positive; Output_Chan : out Channel_Access);
   private
      Thread_Count : Natural := 0;
   end Thread_Manager;

   Manager : Thread_Manager;

   protected body Thread_Manager is
      procedure Create_Next_Thread (Prime_Number : in Positive; Output_Chan : out Channel_Access) is
         New_Thread : Filter_Thread_Access;
      begin
         Thread_Count := Thread_Count + 1;
         Put_Line(Positive'Image(Prime_Number));

         Output_Chan := new Channel;
         New_Thread := new Filter_Thread;
         New_Thread.Start(Prime_Number, Output_Chan);
      end Create_Next_Thread;
   end Thread_Manager;


   task body Filter_Thread is
      My_Prime     : Positive;
      Input_Chan   : Channel_Access;
      Output_Chan  : Channel_Access := null;
      Number       : Natural;
      Should_Stop  : Boolean := False;
   begin
      accept Start (Prime_Number : in Positive; Input_Chan : in Channel_Access) do
         My_Prime := Prime_Number;
         Filter_Thread.Input_Chan := Input_Chan;
      end Start;

      loop
         exit when Should_Stop;

         Input_Chan.Get(Number);

         if Number = 0 then
            if Output_Chan /= null then
               Output_Chan.Put(0);
            end if;
            Should_Stop := True;

         elsif Number mod My_Prime /= 0 then
            if Output_Chan = null then
               Manager.Create_Next_Thread(Number, Output_Chan);
            end if;

            Output_Chan.Put(Number);

         end if;
      end loop;
   end Filter_Thread;


   First_Channel : Channel_Access := new Channel;
   First_Thread  : Filter_Thread_Access;

begin
   Put_Line("--- Start ---");

   First_Thread := new Filter_Thread;
   First_Thread.Start(2, First_Channel);

   for I in 3 .. Upper_Bound loop
      First_Channel.Put(I);
   end loop;

   Put_Line("--- Sending shutdown signal ---");
   First_Channel.Put(0);

   -- TODO: implement proper shutdown
   delay 1.0;

   Put_Line("--- Finished ---");
end Main;
