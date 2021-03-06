package com.qa.cv.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.mongodb.gridfs.GridFsOperations;

import com.qa.cv.SpringMongoConfig;
import com.qa.cv.model.Person;
import com.qa.cv.repo.PersonRepository;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

//@CrossOrigin(origins = "http://192.168.1.113", maxAge=3600)
@RestController
@RequestMapping("/api")
public class PersonController {
	@Autowired
	private PersonRepository repository;
	
	private String storeFile(MultipartFile multipart) {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringMongoConfig.class);
		GridFsOperations gridOperations = (GridFsOperations) ctx.getBean("gridFsTemplate");

		InputStream inputStream = null;
		try {
			inputStream = multipart.getInputStream();
			//inputStream = new FileInputStream("C:\\Users\\Admin\\Desktop\\doc.txt");
			gridOperations.store(inputStream, "doc.txt");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return "fail";
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
					return "fail";
				}
			}
		}
		return "pass";
	}
	
	@PostMapping("/upload")
	public String singleFileUpload(@RequestParam("file") MultipartFile multipart) {
		
		return storeFile(multipart);
	}
	
	@RequestMapping(value = "/people", method = RequestMethod.GET)
	public List<Person> getPeople() {
		return repository.findAll();
	}
	
	@RequestMapping(value = "/people/{id}", method = RequestMethod.PUT)
	  public Person modifyPersonById(@PathVariable String id, @RequestBody Person person) {
	    repository.save(person);
	    return person;
	  }
	
	@RequestMapping(value="/people",method=RequestMethod.POST)
	public Person createPerson(@RequestBody Person person) {
		repository.save(person);
		return person;
	}
	
	@RequestMapping(value = "/people/{id}", method = RequestMethod.GET)
	  public Optional<Person> getPersonById(@PathVariable("id") String id) {
	    return(repository.findById(id));
	    
	}
	
	@RequestMapping(value="/people/{id}",method=RequestMethod.DELETE)
	public Person deletePerson(@PathVariable String id, Person person) {
		repository.delete(person);
		return person;
	}
	
	@RequestMapping(value="/people/{id}/n",method=RequestMethod.POST)
	public Person updateState(@PathVariable("id") String id, @RequestBody String state) {
		return repository.save(repository.findById(id).get().setState(state));
	}
	
	
	@PutMapping(value = "/login")
	public String checkLogin(@Valid @RequestBody Person user) {
		
		System.out.println("sdjfjdxjfdjs");
		
		List<Person> p = repository.findByEmail(user.getEmail());
		
		for (Person o : p) {
			if (o.getPassword().equals(user.getPassword())) {
				return o.getRole();
			}
		}
		return "NOTFOUND";
	}
	
}
