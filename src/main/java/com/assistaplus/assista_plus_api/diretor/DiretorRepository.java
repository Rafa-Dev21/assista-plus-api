package com.assistaplus.assista_plus_api.diretor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// Repository é responsável pelo acesso aos dados do Diretor.
//
// O JpaRepository já fornece automaticamente operações como:
// - salvar
// - buscar todos
// - buscar por ID
// - atualizar
// - excluir
public interface DiretorRepository extends JpaRepository<Diretor, Long> {

    //Busca diretores pelo nome.

    //Containing = procura o texto em qualquer parte do nome.
    //IgnoreCase = ignora diferença entre maiúsculas e minúsculas.

    //Exemplo:
    // /diretores/buscar?nome=duffer

    //Pode encontrar "Matt Duffer" mesmo usando "duffer".
    List<Diretor> findByNomeContainingIgnoreCase(String nome);


    //Busca um diretor pelo ID junto com suas séries.

    //JOIN FETCH faz o Hibernate carregar o diretor
    //e as séries relacionadas na mesma consulta.

    //DISTINCT evita que o diretor apareça duplicado
    //quando ele possui várias séries.
    @Query("""
            SELECT DISTINCT d
            FROM Diretor d
            LEFT JOIN FETCH d.series
            WHERE d.id = :id
            """)
    Optional<Diretor> findByIdComSeries(@Param("id") Long id);
}

