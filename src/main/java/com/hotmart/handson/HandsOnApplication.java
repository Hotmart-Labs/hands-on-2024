package com.hotmart.handson;

import com.hotmart.handson.model.Like;
import com.hotmart.handson.model.Relationship;
import com.hotmart.handson.model.Tweet;
import com.hotmart.handson.model.User;
import com.hotmart.handson.mongodb.mapping.Indexes;
import com.hotmart.handson.service.UserService;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.web.servlet.function.RequestPredicates.GET;
import static org.springframework.web.servlet.function.RouterFunctions.resources;
import static org.springframework.web.servlet.function.RouterFunctions.route;
import static org.springframework.web.servlet.function.ServerResponse.ok;

@Slf4j
@SpringBootApplication
@EnableMongoRepositories
@RequiredArgsConstructor
public class HandsOnApplication implements CommandLineRunner {

	@Value("${hands-on.media.path}")
	private String mediaPath;

	@Value("${hands-on.mongodb.auto-generate}")
	private boolean mongodbAutoGenerate;

	private final MongoTemplate mongoTemplate;

	private final UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(HandsOnApplication.class, args);
	}

	@Bean
	public OpenAPI openAPI(@Value("${springdoc.version}") String appVersion) {
		return new OpenAPI()
				.components(new Components())
				.info(new Info().title("Hands-on 2024 API").version(appVersion));
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public RouterFunction<ServerResponse> timelineRouter(@Value("classpath:/templates/index.html") Resource html) {
		return route(GET("/timeline/**"), request
				-> ok().contentType(MediaType.TEXT_HTML).body(html)
		);
	}

	@Bean
	@SneakyThrows
	public RouterFunction<ServerResponse> hotmartMediaRouter() {
		var medias = List.of(
				"fire.png", "fire-banner.jpg", "hotmart.jpg", "hotmart-banner.jpg",
				"sparkle.jpg", "sparkle-banner.jpg", "teachable.png", "teachable-banner.jpg"
		);
		return resources(request -> {
			var path = request.requestPath().value();
			var file = path.substring(path.lastIndexOf("/") + 1);
			if (path.startsWith("/media") && medias.contains(file)) {
				return Optional.of(new ClassPathResource("/media/" + file));
			}
			return Optional.empty();
		});
	}

	@Bean
	@SneakyThrows
	public RouterFunction<ServerResponse> mediaRouter(@Value("${hands-on.media.path}") String mediaPath) {
		return resources("/media/**", new FileUrlResource(mediaPath));
	}

	@Override
	public void run(String... args) throws Exception {
		if (!mongodbAutoGenerate) {
			log.info("MongoDB structure not generated");
			return;
		}

		var file = Path.of(mediaPath).toFile();
		if (!file.exists() && !file.mkdirs()) {
			throw new IOException("Can't create media directory");
		}

		Stream.of(Tweet.class, User.class, Like.class, Relationship.class).forEach(this::createCollection);

		var user1 = User.builder()
				.username("user1")
				.name("Usuário de Testes")
				.bio("Usuário para execução de testes no Hands-on").build();

		var hotmart = User.builder()
				.username("hotmart")
				.name("Hotmart")
				.link("https://www.hotmart.com/changes/pt-BR")
				.location("Belo Horizonte, MG")
				.picture("hotmart.jpg")
				.banner("hotmart-banner.jpg")
				.bio("#HotmartMudanças\n" +
						"Milhares de pessoas ensinam e aprendem na Hotmart todos os dias. ✨\n" +
						"\n" +
						"Faça como elas e comece a mudar sua história agora mesmo.").build();

		var sparkle = User.builder()
				.username("sparkle")
				.name("Hotmart Sparkle")
				.link("https://hotm.art/sparkle-tt")
				.location("Belo Horizonte, MG")
				.picture("sparkle.jpg")
				.banner("sparkle-banner.jpg")
				.bio("Descubra, participe e crie comunidades de verdade!\n" +
						"Baixe o app Hotmart Sparkle: http://hotm.art/sparkle-tt").build();

		var teachable = User.builder()
				.username("teachable")
				.name("Teachable")
				.link("https://teachable.com")
				.location("New York, NY")
				.picture("teachable.png")
				.banner("teachable-banner.jpg")
				.bio("We’re on a mission to enable the transformative power of knowledge in our world. #ShareWhatYouKnow").build();

		var fireFestival = User.builder()
				.username("firefestival")
				.name("FIRE FESTIVAL")
				.link("https://www.hotmart.com/fire/pt")
				.location("Expominas, Belo Horizonte - MG")
				.picture("fire.png")
				.banner("fire-banner.jpg")
				.bio("Um encontro com empreendedores, creators, especialistas e artistas que estão transformando o mundo.").build();

		List.of(user1, hotmart, sparkle, teachable, fireFestival)
				.forEach(u -> {
					try {
						var user = userService.create(u);
						log.debug("User {} created", user.getUsername());
					} catch (Exception e) {
                        log.warn("User not created: {}", e.getMessage(), e);
					}
				});
	}

	private void createCollection(Class<?> clazz) {
		if (!mongoTemplate.collectionExists(clazz)) {
			var options = CollectionOptions.empty();
			var collectionOptions = clazz.getAnnotation(com.hotmart.handson.mongodb.mapping.CollectionOptions.class);
			if (collectionOptions != null && collectionOptions.capped()) {
				options = options.capped()
						.size(collectionOptions.size())
						.maxDocuments(collectionOptions.maxDocuments());
			}
			mongoTemplate.createCollection(clazz, options);
			var indexes = clazz.getAnnotation(Indexes.class);
			Stream<Index> indexStream = Stream.empty();
			Stream<Index> compoundIndexesStream = Stream.empty();
			if (indexes != null) {
				indexStream = Arrays.stream(indexes.value()).map(i -> {
					var definition = new Index(i.field(), i.direction());
					if (i.unique()) {
						definition.unique();
					}
                    return definition;
                });
			}
			var compoundIndexes = clazz.getAnnotation(CompoundIndexes.class);
			if (compoundIndexes != null) {
				compoundIndexesStream = Arrays.stream(compoundIndexes.value()).map(i -> {
					var definition = new CompoundIndexDefinition(Document.parse(i.def()));
					definition.named(i.name());
					if (i.unique()) {
						definition.unique();
					}
					return definition;
				});
			}
			Stream.concat(indexStream, compoundIndexesStream).forEach(i -> mongoTemplate.indexOps(clazz).ensureIndex(i));
		}
	}
}
