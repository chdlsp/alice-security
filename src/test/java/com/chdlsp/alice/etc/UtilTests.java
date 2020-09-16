package com.chdlsp.alice.etc;


import com.google.gson.JsonParser;
import org.junit.Test;

public class UtilTests {

    @Test
    public void jsonParserTest() {

        String response = "{\"access_token\":\"oeKLzxWQKrZDtIp2T8pkYdmOl1fs2N_rYk4owAopcSEAAAF0lh034A\",\"token_type\":\"bearer\",\"refresh_token\":\"7Zva6de5b0woKwYTuoDL4L92XQCKQE3PsJwHHgopcSEAAAF0lh033g\",\"expires_in\":21599,\"scope\":\"account_email\",\"refresh_token_expires_in\":5183999}";

        JsonParser.parseString(response).isJsonArray();
        JsonParser.parseString(response).getAsJsonArray();


    }
}
