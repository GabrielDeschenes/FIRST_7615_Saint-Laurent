/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **
 * Patbot.cpp																*
 * Compétition FIRST 2020 Équipe 7615										*
 * Par Jean-Michel Harvey-Perron											*
 * Cégep de Saint-Laurent					 								*
 * Créé: 31 Octobre 2019				 									*
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * **/

#include "BigRobot.h"

// Classe principale. Exécutée à chaque 0.02 seconde (timed robot)
class Robot : public frc::TimedRobot
{
	private:

		// Variables:
		float speedfactor;
		char ActionLog[18];
		bool ArmIsMoving = 0;

		// Structure du mode choisi:
		Select_t *Selected;

		// Structures des modes possibles:
		Select_t Selection[3] = 
		{
			{0, "Repos" },
			{1, "Action"},
			{2, "Dev"	}
		};

		// Instances de Hardware FRC:

  		frc::PWMVictorSPX 	rightDrive{1};
  		frc::PWMVictorSPX 	leftDrive{0};
  		frc::PWMVictorSPX 	BallGrab{2};
  		frc::PWMVictorSPX 	Arm{3};

		frc::Ultrasonic 	ballDetect{0,1};
		frc::AnalogInput 	armPot{0};

  		frc::Joystick 		joystick{1};
  		frc::Joystick 		gamepad{0};

 	public:

		// Fonctions de cette classe:	

		void RobotInit()
		{
			ballDetect.SetAutomaticMode(true);
			Selected = &Selection[0]; 
			speedfactor = 0;
		}
		// Contrôle du drive-train (roues)
		void Tank(void)
		{
			// gestion du facteur de vitesse
			if(speedfactor >= 0.1 && gamepad.GetRawButtonPressed(BOUTON_LB)) speedfactor -= .1;
			if(speedfactor <= 0.2 && gamepad.GetRawButtonPressed(BOUTON_RB)) speedfactor += .1;

			// Contrôle selon la position des 2 minis joysticks de la manette
    		leftDrive.Set (speedfactor * gamepad.GetRawAxis(LEFT_STICK_VERT)); 
    		rightDrive.Set(speedfactor * gamepad.GetRawAxis(RIGHT_STICK_VERT) * -1); 
		}
		// Affiche les actions du robot à la driverStation(en mode Normal)
		void driverShell(void)
		{
        	cout 	
				<< "Speedfactor: "	<< (int)(speedfactor * 100) << "%" 
				<< "\tDoing: "		<< ActionLog 
				<< "\tMode: " 		<< Selected->Mode 
									<< endl;
		}
		// Affichage des infos en mode-développeur (Debug)
		void coderShell(void)
		{
			cout 
				<< "PING: "		<< ballDetect.GetRangeInches()
				<< " JOY: "		<< joystick.GetRawAxis(AXE_VERT) 
				<< " ARM: "		<< armPot.GetValue() 
				<< " Mode Num:" << Selected->Num
								<< endl;
		}
		// Contrôle du bras en douceur (selon la position)
		void armControl(void)
		{
			// Monter le bras (Joystick vers Arrière)
			if(joystick.GetRawAxis(AXE_HORIZ) > 0)
			{
				ArmIsMoving = true;

				// Si le bras est en dessous du levier maximum
				if(armPot.GetValue() <= ARM_MAX_LEVER)
				{
					// Monter à vitesse max
					Arm.Set(1.0 * joystick.GetRawAxis(AXE_HORIZ));
				}
				// Ou s'il est en avant de sa position verticale
				else if(armPot.GetValue() <= ARM_VERTICAL)
				{
					// Monter plus lentement
					Arm.Set(0.5 * joystick.GetRawAxis(AXE_HORIZ));
				}
				// Passé la verticale (vers arrière)
				else if(armPot.GetValue() < ARM_MAX_IN)
				{
					// reculer lentement
					Arm.Set(0.1 * joystick.GetRawAxis(AXE_HORIZ));
				}
				// et ne rien arracher
				else
				{
					Arm.Set(0);
				}
			}
			// Descendre le Bras(Joystick vers Avant)
			else if(joystick.GetRawAxis(AXE_HORIZ) < 0)
			{
				ArmIsMoving = true;

				// Monter rapidement (entre full-arrière et Position Verticale)
				if(armPot.GetValue() >= ARM_VERTICAL)
				{
					// Monter assez rapidement
					Arm.Set(.4 * joystick.GetRawAxis(AXE_HORIZ));
				}
				// Et Descendre tranquillement vers l'Avant
				else if(armPot.GetValue() > ARM_MAX_OUT)
				{
					Arm.Set(.1 * joystick.GetRawAxis(AXE_HORIZ));
				}
				// Et ne rien arracher
				else
				{
					Arm.Set(0);
				}
			}
  			// Joystick centré: Ne rien Faire.
			else if(joystick.GetRawAxis(AXE_HORIZ) == 0)
			{
				Arm.Set(0);
				ArmIsMoving = false;
			}
		}
		void stop(void)
		{
    		rightDrive.Set(0); 
    		leftDrive.Set(0); 
			speedfactor = 0;
		}
		// Fonction qui détecte si le ballon est bel et bien attrapé (ultrason)
		bool ballSet(void)
		{
			if(ballDetect.GetRangeInches() > 5.5 && ballDetect.GetRangeInches() < 6.5)
			{
        		strcpy(ActionLog,"Ball Set");
				return 1;
			}
			else 
			{
				return 0;				
			}
		}		
		// Fonction pour agripper ou lancer le ballon
		void Shooter(void)
		{
			// Agripper si aucun ballon dans la pelle
    		if((gamepad.GetRawButton(BOUTON_B) || joystick.GetRawButton(BOUTON_7)) && (ballSet() == 0))
    		{
        		strcpy(ActionLog,"Grabbing Ball");
				BallGrab.Set(-0.5);	
    		}
			// Shooter le ballon (mais pas trop fort (0.8))
			else if(gamepad.GetRawButton(BOUTON_A) || joystick.GetRawButton(BOUTON_8))
    		{
        		strcpy(ActionLog,"Shooting");
				BallGrab.Set(0.8);	
    		}
    		else
    		{
				BallGrab.Set(0);	
			}	
		}
		// Fonction pour choisir le mode de fonctionnement
		void chooseMode(void)
		{
    		if(gamepad.GetRawButtonPressed(BOUTON_START))
			{
    			if(gamepad.GetRawButton(BOUTON_Y)) Selected = &Selection[2]; // Aller en Mode Developpeur(START + Y)
				else if(Selected == &Selection[0]) Selected = &Selection[1]; // Aller en Mode Action(START)
				else Selected = &Selection[0]; 								 // Aller en Mode Repos(START)	
			}
		}
		// Main loop
  		void TeleopPeriodic() override 
  		{
			switch(Selected->Num)
			{
				case 1: 
					Tank();
					Shooter();
					armControl();
					driverShell();
					chooseMode();
					break;
				case 2:
					Tank();
					Shooter();
					armControl();
					coderShell();
					chooseMode();
					driverShell();
					break;
				case 0:
					stop();
					driverShell();
					chooseMode();
					break;
			}				
  		}
};
#ifndef RUNNING_FRC_TESTS
int main(void)
{
    return frc::StartRobot<Robot>();
}
#endif
