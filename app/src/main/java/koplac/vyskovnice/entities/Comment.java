package koplac.vyskovnice.entities;


/**
 * Defines the comment object indicating:
 * the user that wrote it, the date, and the comment
 * @author Antonio Manuel Sanchez Amat
 * @author Juan Jose Espinosa Sanchez
 */
public class Comment {
    private String author;
    private String date;
    private String comment;

    public Comment(String author,String date,String comment){
        this.author=author;
        this.date=date;
        this.comment=comment;
    }

    public String toString(){return author+date+comment;}

    public String getAuthor() {
        return author;
    }
    public String getComment() {
        return comment;
    }
    public String getDate() {
        return date;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setDate(String date) {
        this.date = date;
    }
}
