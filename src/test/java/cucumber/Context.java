package cucumber;

// Add any more enums for the scenario context keys as needed
public enum Context {

    // Global
    RESPONSE, // contains class Response
    WIREMOCK, // contains class WireMockMappingsContext
    TIMESTAMP, // contains class OffsetDateTime

    // Berlinium
    A4_NEG, // contains class NetworkElementGroupDto
    A4_NE, // contains class NetworkElementDto
    A4_NEP, // contains class NetworkElementPortDto
    A4_NEL, // contains class NetworkElementLinkDto
    A4_TP, // contains class TerminationPointDto
    A4_NSP_FTTH, // contains class NetworkServiceProfileFtthAccessDto
    A4_NSP_L2BSA, // contains class NetworkServiceProfileL2BsaDto
    A4_NSP_A10NSP, // contains class NetworkServiceProfileA10NspDto
    A4_CSV, // contains class A4ImportCsvData
    WIREMOCK_COUNT_WG_A4_DEPROV, // contains int
    A4_RESOURCE_ORDER, // contains class ResourceOrder

    // U-Piter
    // ...

}
