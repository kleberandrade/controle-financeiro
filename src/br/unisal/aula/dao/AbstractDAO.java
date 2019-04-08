package br.unisal.aula.dao;

import br.unisal.aula.helpers.EntityManagerUtil;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public abstract class AbstractDAO<T extends Serializable> implements IGenericDAO<T> {

    @PersistenceContext(unitName = EntityManagerUtil.NOME_ENTIDADE_PERSISTENCIA)
    private EntityManager entityManager;

    private final Class persistentClass;

    public AbstractDAO() {
        this.entityManager = EntityManagerUtil.getEntityManager();
        this.persistentClass = (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
    
    protected void openManager(){
        if (entityManager == null || !entityManager.isOpen()){
            entityManager = EntityManagerUtil.getEntityManager();
        }
    }
    
    protected void openTransaction() {
        openManager();
        if (!entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().begin();
        }
    }
    
    protected void commitTransaction() {
        if (entityManager.getTransaction().isActive()) {
            entityManager.getTransaction().commit();
        }
    }
    
    protected void closeIfOpened() {
        if (entityManager.isOpen()) {
            entityManager.close();
        }
    }

    @Override
    public T findById(Long id) {
        T entity = null;
        try {
            openTransaction();
            entity = (T) entityManager.find(persistentClass, id);
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
        return entity;
    }

    @Override
    public List<T> findAll() {
        List<T> entities = null;
        try {
            openTransaction();
            CriteriaBuilder builder = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> criteria = builder.createQuery(persistentClass);
            criteria.from(persistentClass);
            entities = entityManager.createQuery(criteria).getResultList();
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
        return entities;
    }

    @Override
    public void create(T entity) {
        try {
            openTransaction();
            entityManager.persist(entity);
            commitTransaction();
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
    }

    @Override
    public void update(T entity) {
        try {
            openTransaction();
            entityManager.merge(entity);
            commitTransaction();
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
    }

    @Override
    public void delete(T entity) {
        try {
            openTransaction();
            entity = entityManager.merge(entity);
            entityManager.remove(entity);
            commitTransaction();
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
    }
    
    @Override
    public void deleteById(Long id) {
        try {
            openTransaction();
            entityManager.remove(entityManager.getReference(persistentClass, id));
            commitTransaction();
        } catch (Throwable t) {
            t.printStackTrace();
            entityManager.getTransaction().rollback();
        } finally {
            closeIfOpened();
        }
    }
}