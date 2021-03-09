package com.tsystems.tm.acc.ta.team.berlinium;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

@Slf4j
public class A4DeterminateFreeL2bsaTpOnNeg {







    @Test
    @Owner("Heiko.Schwanke@t-systems.com")
    @TmsLink("DIGIHUB-97408")
    @Description("test, Determination of free L2BSA TP on NEG, A4")
    public void testDetermineFreeL2bsaTp() throws InterruptedException {

        // generate test data


        log.info("+++ starte getNegCarrierConnections ");
        // getNegCarrierConnections
        // Einbindung der API, yaml,   über osr.models* ?  Anita baut gerade an carrier-yaml





        // free TP: TP with NSP only in state 'planning'




        // check SperrStatus, name in db?
        // false => free TP were found for a carrier
        // 0 or 1 in db: free TP   ?



        // check;   ok, if ...



        //Thread.sleep(5000);

        log.info("+++ habe fertig ");



    }


}
