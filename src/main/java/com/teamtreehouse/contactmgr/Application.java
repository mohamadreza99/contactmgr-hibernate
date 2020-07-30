package com.teamtreehouse.contactmgr;

import com.teamtreehouse.contactmgr.model.Contact;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

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

        int id = save(contact);

        // display a list of contacts before the update
//        for (Contact c : fetchAllContacts()) {
//            System.out.println(c);
//        }
        System.out.printf("%n%nBefore update%n%n");
        fetchAllContacts().stream().forEach(System.out::println);

        // get the persisted contacts saved to the db and update it and update and persist changes
        Contact c = findContactById(id);
        c.setFirstName("Mike");
        System.out.printf("%n%nUpdating...%n%n");
        update(c);
        System.out.printf("%n%nUpdate Complete!%n%n");
        fetchAllContacts().stream().forEach(System.out::println);

    }

    private static Contact findContactById(int id) {
        Session session = sessionFactory.openSession();
        Contact contact= session.get(Contact.class, id);
        session.close();
        return contact;
    }

    private static void update(Contact contact) {
        Session session = sessionFactory.openSession();

        // begin a transaction
        session.beginTransaction();
        // use the session to update the contact
        session.update(contact);
        // commit the transaction
        session.getTransaction().commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    private static List<Contact> fetchAllContacts() {
        Session session = sessionFactory.openSession();

        // create criteria object
        Criteria criteria = session.createCriteria(Contact.class);
        /*
        we can add some filters to the criteria object like return all records which their email contains
        a specific string but now don't change it
         */

        // create the list of contacts according to the criteria object
        List<Contact> contacts = criteria.list(); // suppress the warning of this line

        session.close();
        return contacts;
    }

    private static int save(Contact contact) {
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
        /*
        session.save() method returns a serializable object that contains the id either assigned auto by the hibernate or
        we give that id in the code
         */
        int id = (int) session.save(contact); // the contact class must be annotated with jpa annotation which we have done

        // commit the transaction to finalize the transaction to the db
        session.getTransaction().commit();

        // close the hibernate session
        session.close();
        return id;
    }


}
