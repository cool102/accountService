package account.businesslayer;

//@Component
//public class EntityService {
//    private EntityManager entityManager;
//    @Autowired
//    public EntityService(EntityManagerFactory entityManagerFactory) {
//        this.entityManager = entityManagerFactory.createEntityManager();
//    }
//
//
//    public UserInfo  setRole(UserInfo userToSetRole) {
//        entityManager.getTransaction().begin();
//        UserInfo userInfo = entityManager.find(UserInfo.class, 1L);
//        if (userInfo == null) {
//            userToSetRole.getUserRoles().add(entityManager.find(Role.class, 1L));
//        }
//        else {
//            userToSetRole.getUserRoles().add(entityManager.find(Role.class, 3L));
//        }
//
//        entityManager.getTransaction().commit();
//        entityManager.clear();
//
//        return userToSetRole;
//
//    }
//    public void insertEntities() {
//
//        entityManager.getTransaction().begin();
//
//        Role admin = new Role(1,"Administrator");
//        Role accountant = new Role(2,"Accountant");
//        Role user = new Role(3,"User");
//        Role anonymous = new Role(4,"Anonymous");
//
//
//
//       //catLeo.setPeopleInContact(Set.of(catLover1, catLover2));
//       //dogCharlie.getPeopleInContact().add(dogLover1);
//       //dogBella.getPeopleInContact().add(dogLover1);
//
//        entityManager.persist(admin);//сохранить
//        entityManager.persist(accountant);
//        entityManager.persist(user);
//        entityManager.persist(anonymous);
//
//        entityManager.getTransaction().commit();
//        entityManager.clear();
//    }
//}
//