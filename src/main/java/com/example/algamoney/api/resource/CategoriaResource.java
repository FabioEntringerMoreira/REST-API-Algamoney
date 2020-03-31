package com.example.algamoney.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.algamoney.api.event.RecursoCriadoEvent;
import com.example.algamoney.api.model.Categoria;
import com.example.algamoney.api.repository.CategoriaRepository;

/*
 *@RestController -> Uma anotação que une a @Controller e a @ResponseBody. Define a
 *a classe como um controller e já faz algumas configurações: como o retorno de um
 *JSON por exemplo.
 *
 * @RequestMapping -> Define a URL que será controlada pela classe.
 */
@RestController
@RequestMapping("/categorias")
public class CategoriaResource {
	
	/*
	 * Injeção de dependência
	 */
	@Autowired
	private CategoriaRepository categoriaReposirory; 
	
	@Autowired
	ApplicationEventPublisher publisher;
	
	/*
	 * @GetMapping -> Requisição GET
	 */
	@GetMapping
	public List<Categoria> listar() {
		//!categorias.isEmpty() ? ResponseEntity.ok(categorias) : ResponseEntity.noContent().build(); "para retornar 204"
		return categoriaReposirory.findAll(); 
	}
	
	/*
	 *ResponseEntity -> Trabalhando com microservice, ResponseEntity para enviar resposta completa, com status, com cabeçalho e corpo.
	 * 
	  ModelAndView-> essa classe é utilizada para especificar a view que será renderizada e quais os dados ela utilizará para isso.
	
	 *@PostMapping -> Requisição Post
	 *
	 *@RequestBody -> O corpo da requisição será usado como objeto 
	 *
	 *HttpServletResponse -> será usado para setar o header
	 *
	 *@Valid -> Diz para minha classe que ela precisa validar usando o BeanValidation (a anotação deve ser feita aonde
	 * eu recebo o objeto que precisa ser validade)
	 */
	
	@PostMapping // 
	public ResponseEntity<Categoria> criar(@Valid @RequestBody Categoria categoria, HttpServletResponse response) {
		Categoria categoriaSalva = categoriaReposirory.save(categoria);
		publisher.publishEvent(new RecursoCriadoEvent(this, response, categoriaSalva.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(categoriaSalva);
	}
	
	/*
	 * @PathVariable -> Recupera o código da URI e passa paga a variável do construtor.
	 */
	@GetMapping("/{codigo}")
	public ResponseEntity<?> buscarPeloCodigo(@PathVariable Long codigo) {
		Categoria categoriaEncontrada = categoriaReposirory.findOne(codigo);
		
		return categoriaEncontrada != null ? ResponseEntity.ok(categoriaEncontrada)
				: ResponseEntity.notFound().build();
	}
	
}

