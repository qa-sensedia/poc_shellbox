package com.br.ApiTeste;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.response.Response;

import br.com.TesteApiHelper.MassaDeDados;
import groovy.json.JsonException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TesteApi extends MassaDeDados {

	public static String accessToken;
	public static String fuellingId;
	public static String x_access_token;

	@Before
	public void configureRestAssured() {
		RestAssured.baseURI = URIPADRAO;
		RestAssured.config();
	}
	
	@Test
	public void dadoQueEnvio001PostAccessTokenQuandoRetornaOkEntaoGuardoEsseAccessToken() throws JsonException {

		Response response = given().auth().preemptive().basic(USERNAMECLIENTIDVALUE, PASSWORD).contentType(CONTENTTYPE)
				.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
				.log().all().body(PAYLOAD).when().post(URIACCESSTOKEN);
		accessToken = response.jsonPath().getString("access_token");
		System.out.println("O Access_Token retornado foi => " + accessToken);

	}

	@Test
	public void dadoQueEnvio002PostFuellingCriandoAbastecimentoNaBaseQdoReceboOkEntaoGuardoNumeroAbastecimento() throws JsonException {
		
		Response response = given()
		.headers("Content-Type",CONTENTTYPE, "Accept-Language", PT ,"client_id", USERNAMECLIENTIDVALUE, "access_token", accessToken )
		.config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false)))
		.log().all().body(PAYLOADFUELLING).when().post(URIPOSTFUELLING);	
		
		fuellingId = response.jsonPath().getString("fuellingId");
		System.out.println("O fuellingId retornado foi => " + fuellingId);
		
	}
	@Test
	public void dadoQueEnvio003PatchFuellingAutorizandoAbastecimentoNaBombaQdoRecebo204EntaoNumAbastecimentoPassaAoEstadoLiberado() throws JsonException {
		
			given().
			headers("Content-Type", CONTENTTYPE,"client_id", USERNAMECLIENTIDVALUE, "access_token", accessToken ).
			config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
			log().all().body(PAYLOADPATCHFUELLING).when().patch(URIPATCHFUELLING + fuellingId).then().statusCode(204);	
		
			System.out.println("O abastecimento nº: " + fuellingId + " foi autorizado");
		
	}

	@Test
	public void dadoQueEnvio004GetConsultandoFuellingAutorizadoQdoPassoNumeroAbastecimentoOReceboComStatus2QueEAutorizado() throws JsonException {
		
		given().
		headers("client_id", USERNAMECLIENTIDVALUE, "access_token", accessToken).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().when().get(URIPATCHFUELLING + fuellingId).then().statusCode(200).body(containsString(STATUS2))
		.body(containsString(fuellingId)).assertThat();
		
		System.out.println("O abastecimento está com Status: " + STATUS2 + " para o abastecimento nº: " + fuellingId);
		
	}

	@Test
	public void dadoQueEnvio005PostLoginAbastecimentoNaBombaQdoObtenhoStatus200EntaoPodereiRealizarConfigDeAbastecimento() throws JsonException {
		
		Response response =
		given().
		headers("Content-Type", CONTENTTYPE).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().body(PAYLOADSHELLBOX).when().post(URISIMULADORSHELLBOX);
		x_access_token = (String) response.asString().subSequence(28, 304);
		System.out.println("O x_access_token é => " + x_access_token);
		
	}
	
	
	@Test
	public void dadoQueEnvio006GetListAbastecimentoNaBombaQdoObtenhoStatus200EntaoPodereiCompletarConfigDeAbastecimento() throws JsonException {
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		
		given().
		headers("Content-Type", CONTENTTYPE, "request-uuid", randomUUIDString, "x-access-token", x_access_token ).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().when().get(URISIMULADORSHELLBOXLIST).then().statusCode(200);
		
	}

	@Test
	public void dadoQueEnvio007PostStartAbastecimentoNaBombaQdoObtenhoStatus200EntaoAdicionoValorDeAbastecimento() throws JsonException {
		
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		given().
		headers("Content-Type", CONTENTTYPE, "request-uuid", randomUUIDString, "session-uuid", SESSIONUUID, "x-access-token", x_access_token ).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().body(PAYLOADSTARTSHELLBOX).when().post(URISTARTABASTECIMENTOSHELLBOX).then().statusCode(200);
		
	}

	@Test
	public void dadoQueEnvio008PostFinishAbastecimentoNaBombaQdoObtenhoStatus200EntaoObtenhoValorTotalDeAbastecimento() throws JsonException {
		
		UUID uuid = UUID.randomUUID();
		String randomUUIDString = uuid.toString();
		
		given().
		headers("Content-Type", CONTENTTYPE, "request-uuid", randomUUIDString, "session-uuid", SESSIONUUID, "x-access-token", x_access_token ).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().body(PAYLOADFINISHSHELLBOX).when().post(URIFINISHABASTECIMENTOSHELLBOX).then().statusCode(200);
		
	}
	
	@Test
	public void dadoQueEnvio009GetConsultandoFuellingAutorizadoQdoPassoNumeroAbastecimentoOReceboComStatus4ConfirmacaoPagtoRealizada() throws JsonException {
		
		given().
		headers("client_id", USERNAMECLIENTIDVALUE, "access_token", accessToken).
		config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
		log().all().when().get(URIPATCHFUELLING + fuellingId).then().statusCode(200).body(containsString(STATUS4))
		.body(containsString(fuellingId)).assertThat();
		
		System.out.println("O abastecimento está com Status Confirmação Pagamento Realizada, com status: " + STATUS4 + " para o abastecimento nº: " + fuellingId);
		
	}
	
	
}