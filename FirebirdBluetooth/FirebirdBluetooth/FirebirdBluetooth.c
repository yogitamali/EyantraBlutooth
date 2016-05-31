

/*
 *
 * Project Name: <Firebird V operation using Android app>
 * Author List: <Archie Mittal,Kanupriya Sharma>
 * Filename: <FirebirdBluetooth>
 * Functions: <<motion_set (unsigned char Direction),spi_pin_config(),lcd_port_config(),adc_pin_config(),buzzer_pin_config(),buzzer_on(),buzzer_off(),motion_pin_config(),port_init(),spi_init(),
 * timer5_init(),adc_init(),ADC_Conversion(),velocity(),forward(),back(),left(),right(),stop(),init_devices(),spi_master_tx_and_rx()>
 * Global Variables: <BATT_VALUE, SHARP_3, SHARP_2, SHARP_4, SHARP_1, SHARP_5,sharp, distance, adc_reading,proxy1,proxy2,proxy3,proxy4,proxy5,proxy6,proxy7,proxy8,white1,white2,white3,ADC_Value,
 * leftVelocityTemp1, leftVelocityFlag1, leftVelocityFlag2 , leftVelocityTemp2,rightVelocityTemp1,rightVelocityFlag1, rightVelocityFlag2, rightVelocityTemp2,leftVelocity, rightVelocity,unsigned char data,velTemp, flag, flag2, velTemp2,vel, vel2>
 *
 */
/**********************************************************************************************************************************************/
#define F_CPU 14745600
#include <avr/io.h>
#include <avr/interrupt.h>
#include <util/delay.h>

/**********************************************************************************************************************************************/
unsigned char data; //to store received data from UDR1
static volatile int velTemp = 0, flag =0, flag2 =0, velTemp2 = 0;
unsigned char vel, vel2;
static volatile int leftVelocityTemp1 = 0, leftVelocityFlag1 =0, leftVelocityFlag2 =0, leftVelocityTemp2 = 0;
static volatile int rightVelocityTemp1 = 0, rightVelocityFlag1 =0, rightVelocityFlag2 =0, rightVelocityTemp2 = 0;
static volatile unsigned char leftVelocity, rightVelocity;
unsigned char BATT_VALUE, SHARP_3, SHARP_2, SHARP_4, SHARP_1, SHARP_5,sharp, distance, adc_reading,proxy1,proxy2,proxy3,proxy4,proxy5,proxy6,proxy7,proxy8,white1,white2,white3,ADC_Value;
/**********************************************************************************************************************************************/
/*
 *
 * Function Name:	<spi_pin_config () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used configure spi pin>
 * Example Call:	<spi_pin_config ()>
 *
 */

void spi_pin_config (void)
{
	DDRB = DDRB | 0x07;
	PORTB = PORTB | 0x07;
}
/**********************************************************************************************************************************************/
/*
 *
 * Function Name:	<adc_pin_config () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used configure ADC pins>
 * Example Call:	<adc_pin_config ()>
 *
 */

void adc_pin_config (void)
{
	DDRF = 0x00; //set PORTF direction as input
	PORTF = 0x00; //set PORTF pins floating
	DDRK = 0x00; //set PORTK direction as input
	PORTK = 0x00; //set PORTK pins floating
}
/*********************************************************/
/*
 *
 * Function Name:	<adc_init()>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to initialize adc conversion pins>
 * Example Call:	<adc_init()>
 *
 */
void adc_init()
{
	ADCSRA = 0x00;
	ADCSRB = 0x00;		//MUX5 = 0
	ADMUX = 0x20;		//Vref=5V external --- ADLAR=1 --- MUX4:0 = 0000
	ACSR = 0x80;
	ADCSRA = 0x86;		//ADEN=1 --- ADIE=1 --- ADPS2:0 = 1 1 0
}
/*********************************************************/
/*
 *
 * Function Name:	<spi_init()>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used configure spi ports>
 * Example Call:	<spi_init()>
 *
 */
void spi_init(void)
{
	SPCR = 0x53; //setup SPI
	SPSR = 0x00; //setup SPI
	SPDR = 0x00;
}

/*********************************************************/
/*
 *
 * Function Name:	<spi_master_tx_and_rx () >
 * Input:			<unsigned char data>
 * Output:			<rx_data:ADC channel data from slave microcontroller>
 * Logic:			<This function is used to send byte to the slave micro controller and get ADC channel data from the slave microcontroller>
 * Example Call:	< spi_master_tx_and_rx (5)>
 *
 */
unsigned char spi_master_tx_and_rx (unsigned char data)
{
	unsigned char rx_data = 0;

	PORTB = PORTB & 0xFE; // make SS pin low
	SPDR = data;
	while(!(SPSR & (1<<SPIF))); //wait for data transmission to complete

	_delay_ms(1); //time for ADC conversion in the slave microcontroller
	
	SPDR = 0x50; // send dummy byte to read back data from the slave microcontroller
	while(!(SPSR & (1<<SPIF))); //wait for data reception to complete
	rx_data = SPDR;
	PORTB = PORTB | 0x01; // make SS high
	return rx_data;
}
/*********************************************************/
/*
 *
 * Function Name:	<ADC_Conversion()>
 * Input:			<unsigned char Ch>
 * Output:			<none>
 * Logic:			<This Function accepts the Channel Number and returns the corresponding Analog Value>
 * Example Call:	<ADC_Conversion(11)>
 *
 */
unsigned char ADC_Conversion(unsigned char Ch)
{
	unsigned char a;
	if(Ch>7)
	{
		ADCSRB = 0x08;
	}
	Ch = Ch & 0x07;
	ADMUX= 0x20| Ch;
	ADCSRA = ADCSRA | 0x40;		//Set start conversion bit
	while((ADCSRA&0x10)==0);	//Wait for ADC conversion to complete
	a=ADCH;
	ADCSRA = ADCSRA|0x10; //clear ADIF (ADC Interrupt Flag) by writing 1 to it
	ADCSRB = 0x00;
	return a;
}
/********************************************************/
/*
 *
 * Function Name:	<buzzer_pin_config () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used configure buzzer pins>
 * Example Call:	<buzzer_pin_config ()>
 *
 */

void buzzer_pin_config (void)
{
	DDRC = DDRC | 0x08;		//Setting PORTC 3 as outpt
	PORTC = PORTC & 0xF7;		//Setting PORTC 3 logic low to turnoff buzzer
}
/*********************************************************/

/*
 *
 * Function Name:	<motion_pin_config () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to configure ports to enable robot's motion>
 * Example Call:	<motion_pin_config ()>
 *
 */
void motion_pin_config (void) 
{
 DDRA = DDRA | 0x0F;
 PORTA = PORTA & 0xF0;
 DDRL = DDRL | 0x18;   //Setting PL3 and PL4 pins as output for PWM generation
 PORTL = PORTL | 0x18; //PL3 and PL4 pins are for velocity control using PWM.
}

/**********************************************************************/
// Timer 5 initialized in PWM mode for velocity control
// Prescale:256
// PWM 8bit fast, TOP=0x00FF
// Timer Frequency:225.000Hz
/*
 *
 * Function Name:	<timer5_init()>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to control velocity in 8 bit fast PWM mode>
 * Example Call:	<timer5_init()>
 *
 */

void timer5_init()
{
	TCCR5B = 0x00;	//Stop
	TCNT5H = 0xFF;	//Counter higher 8-bit value to which OCR5xH value is compared with
	TCNT5L = 0x01;	//Counter lower 8-bit value to which OCR5xH value is compared with
	OCR5AH = 0x00;	//Output compare register high value for Left Motor
	OCR5AL = 0xFF;	//Output compare register low value for Left Motor
	OCR5BH = 0x00;	//Output compare register high value for Right Motor
	OCR5BL = 0xFF;	//Output compare register low value for Right Motor
	OCR5CH = 0x00;	//Output compare register high value for Motor C1
	OCR5CL = 0xFF;	//Output compare register low value for Motor C1
	TCCR5A = 0xA9;	/*{COM5A1=1, COM5A0=0; COM5B1=1, COM5B0=0; COM5C1=1 COM5C0=0}
 					  For Overriding normal port functionality to OCRnA outputs.
				  	  {WGM51=0, WGM50=1} Along With WGM52 in TCCR5B for Selecting FAST PWM 8-bit Mode*/
	
	TCCR5B = 0x0B;	//WGM12=1; CS12=0, CS11=1, CS10=1 (Prescaler=64)
}
/*********************************************************/
/*
 *
 * Function Name:	<velocity ()>
 * Input:			<unsigned char left_motor, unsigned char right_motor>
 * Output:			<none>
 * Logic:			<This function is used for setting the value of velocity for the left and right motor respectively >
 * Example Call:	<velocity(50,50)>
 *
 */

void velocity (unsigned char left_motor, unsigned char right_motor)
{
	OCR5AL = (unsigned char)left_motor;
	OCR5BL = (unsigned char)right_motor;
}
/*********************************************************/
/*
 *
 * Function Name:	< motion_set ()>
 * Input:			<unsigned char Direction>
 * Output:			<none>
 * Logic:			<This function used for setting motor's direction >
 * Example Call:	<motion_set (0x06)>
 *
 */

void motion_set (unsigned char Direction)
{
 unsigned char PortARestore = 0;

 Direction &= 0x0F; 		// removing upper nibbel for the protection
 PortARestore = PORTA; 		// reading the PORTA original status
 PortARestore &= 0xF0; 		// making lower direction nibbel to 0
 PortARestore |= Direction; // adding lower nibbel for forward command and restoring the PORTA status
 PORTA = PortARestore; 		// executing the command
}
/*********************************************************/
/*
 *
 * Function Name:	< forward() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function used for setting both wheels forward hence having forward motion>
 * Example Call:	< forward()>
 *
 */

void forward (void) //both wheels forward
{
  motion_set(0x06);
}
/*********************************************************/
/*
 *
 * Function Name:	< back() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function used for setting both wheels backward hence having backward motion>
 * Example Call:	< back()>
 *
 */
void back (void) //both wheels backward
{
  motion_set(0x09);
}
/*********************************************************/
/*
 *
 * Function Name:	< left() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function used for setting Left wheel backward and Right wheel forward hence having left motion>
 * Example Call:	< left()>
 *
 */

void left (void) //Left wheel backward, Right wheel forward
{
  motion_set(0x05);
}
/*********************************************************/
/*
 *
 * Function Name:	< right() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function used for setting Left wheel forward and Right wheel backward hence having right motion>
 * Example Call:	< right()>
 *
 */

void right (void) //Left wheel forward, Right wheel backward
{
  motion_set(0x0A);
  
}
/*********************************************************/
/*
 *
 * Function Name:	< stop() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function used for stoping both left and right motors>
 * Example Call:	< stop()>
 *
 */

void stop (void)
{
  motion_set(0x00);
}
/*********************************************************/
/*
 *
 * Function Name:	<buzzer_on () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to turn on the buzzer by setting PIN 3 OF PORTC as output>
 * Example Call:	<buzzer_on()>
 *
 */
void buzzer_on (void)
{
	unsigned char port_restore = 0;
	port_restore = PINC;
	port_restore = port_restore | 0x08;
	PORTC = port_restore;
}
/*********************************************************/
/*
 *
 * Function Name:	<buzzer_off () >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to turn off the buzzer by setting PIN 3 OF PORTC as logic 0>
 * Example Call:	<buzzer_on()>
 *
 */
void buzzer_off (void)
{
	unsigned char port_restore = 0;
	port_restore = PINC;
	port_restore = port_restore & 0xF7;
	PORTC = port_restore;
}
/*********************************************************/
/*
 *
 * Function Name:	<uart3_init(void)>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to initialise UART3>
 * Example Call:	<uart3_init()>
 *
 */

void uart3_init(void)
{
	UCSR3B = 0x00; //disable while setting baud rate
	UCSR3A = 0x00;
	UCSR3C = 0x06;
	UBRR3L = 0x5F; //set baud rate lo
	UBRR3H = 0x00; //set baud rate hi
	UCSR3B = 0xd8;
}
/************************************************************************************/
/*
 *
 * Function Name:	<port_init()>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used to Initialize all the PORTS>
 * Example Call:	<port_init()>
 *
 */
void port_init()
{
	motion_pin_config(); //robot motion pins config
	buzzer_pin_config();
	adc_pin_config();
	spi_pin_config();
}
/************************************************************************************/
/*
 *
 * Function Name:	<SIGNAL(SIG_USART3_RECV)>
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is an interrupt which sends and receives data from UDR3 register>
 * Example Call:	<SIGNAL(SIG_USART3_RECV)>
 *
 */

SIGNAL(SIG_USART3_RECV)	// ISR for receive complete interrupt
{
	data = UDR3; 

	if(leftVelocityFlag1 == 1)
	{
		leftVelocity = data;
		data = 0x79;
		leftVelocityTemp1++;
	}
	
	if(leftVelocityFlag2 == 1)
	{
		leftVelocity = data ;
		leftVelocity = leftVelocity * 2;
		data = 0x7A;
		leftVelocityTemp2++;
	}
	
	if(rightVelocityFlag1 == 1)
	{
		rightVelocity = data ;
		data = 0x41;
		rightVelocityTemp1++;
	}
	
	if(rightVelocityFlag2 == 1)
	{
		rightVelocity = data;
		rightVelocity = rightVelocity * 2;
		data = 0x42;
		rightVelocityTemp2++;
	}
	
	if(data == 0x61) //ASCII value of a
	{
		forward();  //forward
	}

	if(data == 0x62) //ASCII value of b
	{
		back(); //back
	}

	if(data == 0x63) //ASCII value of c
	{
		left();  //left
	}

	if(data == 0x64) //ASCII value of d
	{
		right(); //right
	}

	if(data == 0x65) //ASCII value of e
	{
		stop(); //stop
	}

	if(data == 0x66) //ASCII value of f
	{
		buzzer_on();
	}

	if(data == 0x67) //ASCII value of g
	{
		buzzer_off();
	}
	

	if(data == 0x68) //ASCII value of h
	{
		UDR3=proxy1;
			  
	}
	
	if(data == 0x69) //ASCII value of i
	{
		UDR3=proxy2;   
	}
	
	if(data == 0x6A) //ASCII value of j
	{
		UDR3=proxy3;		   
	}
	
	if(data == 0x6B) //ASCII value of k
	{
		UDR3=proxy4;		     
	}
	
	if(data == 0x6C) //ASCII value of l
	{
		UDR3=proxy5;			  
	}
	
	if(data == 0x6D) //ASCII value of m
	{
		UDR3=proxy6;				    
	}
	
	if(data == 0x6E) //ASCII value of n
	{
		UDR3=proxy7;			 
	}
	
	if(data == 0x6F) //ASCII value of o
	{
		UDR3=proxy8;				  
	}
	
	if(data == 0x70) //ASCII value of p
	{
		UDR3=white1;				   
	}
	
	if(data == 0x71) //ASCII value of q
	{
		UDR3=white2; 				      
	}
					      
	if(data == 0x72) //ASCII value of r
	{					  
		UDR3=white3;    				   
	}
	
	if(data == 0x73) //ASCII value of s
	{ 
		UDR3=SHARP_1;    			   	   
	}
	
	if(data == 0x74) //ASCII value of t
	{
		UDR3=SHARP_2;    
	}
	
	if(data == 0x75) //ASCII value of u
	{
		UDR3=SHARP_3;  
	}
	
	if(data == 0x76) //ASCII value of v
	{
		UDR3=SHARP_4;    
	}
	
	if(data == 0x77) //ASCII value of w
	{
		UDR3=SHARP_5;    
	}
	
	if(data == 0x78) //ASCII value of x
	{
		UDR3=BATT_VALUE;    
	}
	
	if(data == 0x79) //ASCII value of t
	{
		
		leftVelocityFlag1 = 1;
		if(leftVelocityTemp1 == 1)
		{
			leftVelocityFlag1 = 0;
			leftVelocityTemp1 = 0;
		}
	}	
	
	if(data == 0x7A)	//ASCII value of z
	{
		leftVelocityFlag2 = 1;
		if(leftVelocityTemp2 == 1)
		{
			leftVelocityFlag2 = 0;
			leftVelocityTemp2 = 0;
		}
	}
	
	if(data == 0x41) //ASCII value of A
	{
		
		rightVelocityFlag1 = 1;
		if(rightVelocityTemp1 == 1)
		{
			rightVelocityFlag1 = 0;
			rightVelocityTemp1 = 0;
			velocity(leftVelocity, rightVelocity);
		}
	}
	
	if(data == 0x42)
	{
		rightVelocityFlag2 = 1;
		if(rightVelocityTemp2 == 1)
		{
			rightVelocityFlag2 = 0;
			rightVelocityTemp2 = 0;
			velocity(leftVelocity, rightVelocity);
		}
	}
					    
		   
}
/*********************************************************/
/*
 *
 * Function Name:	<init_devices() >
 * Input:			<void>
 * Output:			<none>
 * Logic:			<This function is used for initializing all the devices and ports>
 * Example Call:	< init_devices()>
 *
 */
void init_devices()
{
 cli(); //Clears the global interrupt
 port_init();  //Initializes all the ports
 uart3_init();
 spi_init();
 timer5_init();
 adc_init();
 sei();   // Enables the global interrupt 
}


//Main Function

int main(void)
{
	    init_devices();
		 
	while(1)
	{
		SHARP_1 = ADC_Conversion(9);                        //Analog value of sharp range Sensor 1
		SHARP_2 = ADC_Conversion(10);                       //Analog value of sharp range Sensor 2
		SHARP_3 = ADC_Conversion(11);                       //Analog value of sharp range Sensor 3
		SHARP_4 = ADC_Conversion(12);                       //Analog value of sharp range Sensor 4
		SHARP_5 = ADC_Conversion(13);                       //Analog value of sharp range Sensor 5
		BATT_VALUE = ADC_Conversion(0);                     //Battery voltage status
		proxy1=ADC_Conversion(4);							//Analog value of IR Proximity Sensor 1
		proxy2=ADC_Conversion(5);							//Analog value of IR Proximity Sensor 2
		proxy3=ADC_Conversion(6);							//Analog value of IR Proximity Sensor 3
		proxy4=ADC_Conversion(7);                           //Analog value of IR Proximity Sensor 4
		proxy5=ADC_Conversion(8);                           //Analog value of IR Proximity Sensor 5
		 proxy6 = spi_master_tx_and_rx(5);                  //Analog value of IR Proximity Sensor 6
		 proxy7 = spi_master_tx_and_rx(6);                 //Analog value of IR Proximity Sensor 7
		 proxy8 = spi_master_tx_and_rx(7);                 //Analog value of IR Proximity Sensor 8
		white1=ADC_Conversion(3);							//Analog value of White Line Sensor1
		white2=ADC_Conversion(2);							//Analog Value of White Line Sensor2
		white3=ADC_Conversion(1);							//Analog Value of White Line Sensor3
       
		vel = OCR5AL;
		vel2 = OCR5AL;
		
	}
}