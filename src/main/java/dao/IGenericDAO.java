package dao;

import java.util.List;

/**
 * Interface genérica para operações de acesso a dados (DAO).
 *
 * @param <T> O tipo da entidade.
 * @param <K> O tipo da chave primária da entidade.
 */
public interface IGenericDAO<T, K> {

    /**
     * Salva uma nova entidade no banco de dados.
     * @param entity A entidade a ser salva.
     */
    void save(T entity);

    /**
     * Atualiza uma entidade existente no banco de dados.
     * @param entity A entidade a ser atualizada.
     */
    void update(T entity);

    /**
     * Remove uma entidade do banco de dados pelo seu ID.
     * @param id O ID da entidade a ser removida.
     */
    void delete(K id);

    /**
     * Busca uma entidade pelo seu ID.
     * @param id O ID da entidade a ser buscada.
     * @return A entidade encontrada, ou null se não existir.
     */
    T findById(K id);

    /**
     * Retorna todas as instâncias da entidade.
     * @return Uma lista com todas as entidades.
     */
    List<T> findAll();
}