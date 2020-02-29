/* * * * * * * * * * * * * * * * * * * * * * * **
 * BigRobot.h									*
 * FRC 2020 équipe 7615 						*
 * Jean-Michel Harvey-Perron					*
 * Cégep de Saint-Laurent					 	*
 * Créé le: 1 Novembre 2019					 	*
 * * * * * * * * * * * * * * * * * * * * * * * **/

#ifndef ROBORIO_H
#define ROBORIO_H

//Librairies
#include <frc/PWMVictorSPX.h>
#include <frc/AnalogInput.h>
#include <frc/Ultrasonic.h>
#include <frc/TimedRobot.h>
#include <frc/Joystick.h>
#include <iostream>
#include <frc/smartdashboard/smartdashboard.h>
#include <frc/util/color.h>
#include "rev/ColorSensorV3.h"
#include "rev/ColorMatch.h"
#include "rev/SparkMax.h"

//Définition de type structure pour les différents modes
typedef struct
{	
	uint8_t Num;
	char Mode[7];
}
Select_t;

// Manette:
#define BOUTON_X 			1
#define BOUTON_A 			2
#define BOUTON_B 			3
#define BOUTON_Y 			4
#define BOUTON_LB			5
#define BOUTON_RB			6
#define BOUTON_LT			7
#define BOUTON_RT			8
#define BOUTON_BACK 		9
#define BOUTON_START 		10

#define LEFT_STICK_HORIZ  	0
#define LEFT_STICK_VERT	  	1
#define RIGHT_STICK_HORIZ 	2
#define RIGHT_STICK_VERT  	3
#define ARROW_VERT		  	"?"
#define ARROW_HORIZ		  	"?"

// Joystick:
#define BOUTON_TRIGGER 		1
#define BOUTON_THUMB_TOUCH 	2
#define BOUTON_3			3
#define BOUTON_4			4
#define BOUTON_5			5
#define BOUTON_6			6
#define BOUTON_7			7
#define BOUTON_8			8
#define BOUTON_9			9
#define BOUTON_10			10
#define BOUTON_11			11
#define BOUTON_12			12

#define AXE_HORIZ  			0 
#define AXE_VERT   			1
#define AXE_TWIST  			2
#define AXE_SLIDER 			3	

// Positions Clées du Bras(Valeurs Pot)
#define ARM_MAX_LEVER 2233
#define ARM_VERTICAL  3400
#define ARM_MAX_OUT   1224
#define ARM_MAX_IN    4000

// Écart Maximal du Bras (abs)
#define ARM_MAX_ERROR 2776

// default for the Timed Robot class (Can be Modified)
#define TIMED_ROBOT_DT 0.02 

using namespace std;
#endif //ROBORIO_H
