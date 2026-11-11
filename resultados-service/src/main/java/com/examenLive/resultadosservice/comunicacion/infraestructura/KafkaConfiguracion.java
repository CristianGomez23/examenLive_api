package com.examenLive.resultadosservice.comunicacion.infraestructura;


import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
@Configuration
public class KafkaConfiguracion {
    @Bean NewTopic intentosFinalizados() { return TopicBuilder.name("examenes.intentos-finalizados.v1").partitions(3).replicas(1).build(); }
    @Bean NewTopic resultadosGenerados() { return TopicBuilder.name("resultados.generados.v1").partitions(3).replicas(1).build(); }
    @Bean DefaultErrorHandler erroresKafka() {
        // No descartar eventos tras N intentos: un fallo permanente requiere intervención.
        var handler=new DefaultErrorHandler(new FixedBackOff(2000,FixedBackOff.UNLIMITED_ATTEMPTS));
        handler.addRetryableExceptions(Exception.class);
        return handler;
    }
}
