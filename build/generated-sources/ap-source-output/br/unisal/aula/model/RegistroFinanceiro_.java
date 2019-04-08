package br.unisal.aula.model;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.0.v20130507-rNA", date="2019-04-02T22:17:30")
@StaticMetamodel(RegistroFinanceiro.class)
public class RegistroFinanceiro_ { 

    public static volatile SingularAttribute<RegistroFinanceiro, Integer> id;
    public static volatile SingularAttribute<RegistroFinanceiro, String> categoria;
    public static volatile SingularAttribute<RegistroFinanceiro, Double> valor;
    public static volatile SingularAttribute<RegistroFinanceiro, String> tipo;
    public static volatile SingularAttribute<RegistroFinanceiro, Date> data;
    public static volatile SingularAttribute<RegistroFinanceiro, String> descricao;

}