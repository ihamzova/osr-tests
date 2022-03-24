package com.tsystems.tm.acc.ta.helpers.upiter;

import com.tsystems.tm.acc.tests.osr.access.line.resource.inventory.v5_35_0.client.model.ProfileState;
import com.tsystems.tm.acc.tests.osr.ont.olt.orchestrator.v2_16_0.client.model.SubscriberNeProfiesStates;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

import java.util.function.Function;

public class MapperFunctions {

        private static final ModelMapper modelMapper;

        static {
            modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setMatchingStrategy(MatchingStrategies.STRICT)
                    .setSkipNullEnabled(true);
        }

        /**
         * Mapper for transforming assignOnuIdTask to search DTO
         *
         * @return function for mapping
         */
        public static Function<ProfileState, SubscriberNeProfiesStates> statusMapper() {
            return status -> modelMapper.map(status, SubscriberNeProfiesStates.class);
        }
}
