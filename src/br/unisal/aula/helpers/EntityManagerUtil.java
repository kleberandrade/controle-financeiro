package br.unisal.aula.helpers;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class EntityManagerUtil {
    
    public static final String NOME_ENTIDADE_PERSISTENCIA = "ControleFinanceiroPU";
    
    private static EntityManagerFactory factory;
    
    public static EntityManager getEntityManager(){
        if (factory == null){
            factory = Persistence.createEntityManagerFactory(
                    NOME_ENTIDADE_PERSISTENCIA);
        }
        
        return factory.createEntityManager();
    }
}
