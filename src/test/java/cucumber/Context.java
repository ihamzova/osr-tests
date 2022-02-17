package cucumber;

// Add any more enums for the scenario context keys as needed
public enum Context {

    // Global
    RESPONSE,
    WIREMOCK,
    START_TIMESTAMP,

    // Berlinium
    A4_NEG,
    A4_NE,
    A4_NE_B, // when you need a 2nd NE for your scenario
    A4_NEP,
    A4_NEL,
    A4_TP,
    A4_NSP_FTTH,
    A4_NSP_L2BSA,
    WIREMOCK_COUNT_WG_A4_DEPROV,

    // U-Piter
    TP_REF

}
