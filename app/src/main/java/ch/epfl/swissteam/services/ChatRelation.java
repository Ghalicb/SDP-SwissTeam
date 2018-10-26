package ch.epfl.swissteam.services;


import com.google.firebase.database.DatabaseReference;

/**
 * Class to represent chatRelation between users.
 *
 * @author Sébastien Gachoud
 */
public class ChatRelation implements DBSavable {

    public static final String RELATION_ID_TEXT = "relationId";


    //Attributes
    private String firstUserId_;
    private String secondUserId_;
    private String id_;

    public ChatRelation(User firstUser, User secondUser ){
        this();
        setUsers(firstUser, secondUser);
    }

    public ChatRelation(){
        id_ = null;
    }

    //Getters
    public String getFirstUserId_() {
        return firstUserId_;
    }

    public String getSecondUserId_() {
        return secondUserId_;
    }

    public String getId_() {
        return id_;
    }

    //Setters
    public void setFirstUserId_(String user1Id_) {
        if(secondUserId_ == null){
            setUsersId(user1Id_, user1Id_);
        }
        else{
            setUsersId(user1Id_, secondUserId_);
        }
    }

    public void setSecondUserId_(String secondUserId_) {
        if(firstUserId_ == null){
            setUsersId(secondUserId_, secondUserId_);
        }
        else{
            setUsersId(firstUserId_, secondUserId_);
        }
    }

    public void setId_(String id) {
        this.id_ = id;
    }

    public void setUsers(User firstUser, User secondUser){
        if(firstUser == null){
            throw new NullPointerException("ChatRelations requires non null firstUser");
        }
        if(secondUser == null){
            throw new NullPointerException("ChatRelations requires non null secondUser");
        }

        setUsersId(firstUser.getGoogleId_(), secondUser.getGoogleId_());
    }

    private void setUsersId(String firstId, String secondId){
        if(firstId == null){
            throw new NullPointerException("ChatRelations requires non null firstUser googleId");
        }
        if(secondId == null){
            throw new NullPointerException("ChatRelations requires non null secondUser googleId");
        }

        if(firstId.compareTo(secondId) <= 0) {
            firstUserId_ = firstId;
            secondUserId_ = secondId;
        }
        else{
            firstUserId_ = secondId;
            secondUserId_ = firstId;
        }
    }

    public String getOtherId(String currentUserId){
        if(!isInThisRelation(currentUserId)) {
            throw new IllegalArgumentException("The current user does not belong to this ChatRelation");
        }
        if(firstUserId_.compareTo(currentUserId) == 0){
            return secondUserId_;
        }
        else{
            return firstUserId_;
        }
    }

    private Boolean isInThisRelation(String id){
        return firstUserId_.compareTo(id) == 0 || secondUserId_.compareTo(id) == 0;
    }


    //Overrides
    @Override
    public void addToDB(DatabaseReference databaseReference) {
        if(id_ == null) {
            id_ = databaseReference.child(DBUtility.get().CHATS_RELATIONS).push().getKey();
        }
        databaseReference.child(DBUtility.get().CHATS_RELATIONS).child(id_).setValue(this);
    }
}
