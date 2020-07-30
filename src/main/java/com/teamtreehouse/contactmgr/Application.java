package com.teamtreehouse.contactmgr;

import com.teamtreehouse.contactmgr.model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

public class Application {
    // a static unique reference to SessionFactory (since it's heavy object and we need only one)
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        // create a standardServiceRegistry
        // here we can import data from a sql db
        /* .configure() method loads the hibernate cofig file from its default location which is a file name hibenate.cfg.xml on the
        classpath
        a different location for that file could be specified by including a string parameter in configure()
         */
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();


        /*
        metadataSources object loads the jpa annotated entities that we map in the hibernate config file
        -> Contact entity in this project
         */
        /*
        metadata object has the all the ORM mapping loaded from the annotated entities
         */
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Contact contact = new Contact.ContactBuilder("Chris", "Ramacciotti")
                .withEmail("rama@teamtreehouse.com")
                .withPhone(7735556666L)
                .build();

        // open session
        Session session = sessionFactory.openSession();

        // begin a transaction
        /*
        a transaction is a thing that we use to contain several queries into a single operation
        so if one query fails, we abort the whole transaction by undoing all the previous queries within tha transaction
        this called ROLLING BACK the transaction
         */
        session.beginTransaction();

        // use the session to save the contact
        session.save(contact); // the contact class must be annotated with jpa annotation which we have done

        // commit the transaction to finalize the transaction to the db
        session.getTransaction().commit();

        // close the hibernate session
        session.close();
    }
}
