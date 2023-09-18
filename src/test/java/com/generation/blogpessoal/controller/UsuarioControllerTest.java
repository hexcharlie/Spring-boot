package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UsuarioControllerTest {

	@Autowired
	private TestRestTemplate testRestTemplate;
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll
	void start() {
		usuarioRepository.deleteAll();
		
		usuarioService.cadastrarUsuario(new Usuario (0L,"Root","root@root","", null, null));
	}
	
	@Test
	@DisplayName("Vou cadastrar um usuario!")
	public void deveCriarUmUsuario () {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"Matheus Bergamota","matheus_mexirica@enois.com.br","12345678","-", null));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar",HttpMethod.POST,corpoRequisicao,Usuario.class);
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Verificar a duplicação do usuario")
	public void naoDuplicar () {
		usuarioService.cadastrarUsuario(new Usuario(0L,"MauMau","risadinha@haha.com","123456789","-", null));
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,"MauMau","risadinha@haha.com","123456789","-", null));
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar",HttpMethod.POST,corpoRequisicao,Usuario.class);
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar usuario")
	public void deveAtualizarUsuario() {
		Optional<Usuario> usuarioCadastro = usuarioService.cadastrarUsuario(new Usuario(0L,"Luis","lu@is.gov.com.br","123456789","-", null));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastro.get().getId(),"Luis Henrique","luishenrique@mail.com","123456789","-", null);
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate);
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.withBasicAuth("root@root.com","rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class);
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());			
	}
	
	@Test
	@DisplayName("Listar todos os usuarios")
	public void deveMostrarUsuarios() {
		usuarioService.cadastrarUsuario(new Usuario(0L,"Jonas Whale","jonas@email.com","12345","-",null));
		usuarioService.cadastrarUsuario(new Usuario(0L,"Fernando","fer@email.com","12345","-",null));
		
		ResponseEntity<String> resposta = testRestTemplate
				.withBasicAuth("root@root.com","rootroot")
				.exchange("/usuarios/all", HttpMethod.GET, null, String.class);		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());	
	}
}
