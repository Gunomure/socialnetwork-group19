package ru.skillbox.diplom.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.skillbox.diplom.model.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import ru.skillbox.diplom.websocket.structs.MessageDBComplexResponce;


import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

    Optional<Message> findByRecipientId(Long recipientId);

    @Query(value=
            " WITH mm AS " +
             " ( " +
                    "SELECT   "+
                    " m2.id AS id, m2.time As time, m2.author_id As author_id, "+
                    " m2.recipient_id AS recipient_id, m2.message_text AS message_text, "+
                    " m2.read_status AS read_status, m2.is_deleted As is_deleted, " +
                    " lastmes_in.maxtime AS maxtime_in, lastmes_out.maxtime AS maxtime_out, "+
                    " us2.first_name AS first_name, per2.photo AS photo, "+
                    " count_only_sent_in.unread AS unreadi, count_only_sent_out.unread AS unreado "+
                    " FROM message AS m2 "+

                    "LEFT OUTER JOIN "+
                    "(SELECT MAX(time) AS maxtime, author_id AS authr "+
                    "FROM message  "+
                    "WHERE recipient_id =:recipientId "+
                    "GROUP BY author_id )  lastmes_in "+
                    "ON m2.author_id=lastmes_in.authr AND m2.time=lastmes_in.maxtime  "+

                    "LEFT OUTER JOIN "+
                    "(SELECT MAX(time) AS maxtime, recipient_id AS authr "+
                    "FROM message  "+
                    "WHERE author_id =:recipientId "+//me
                    "GROUP BY recipient_id )  lastmes_out "+
                    "ON m2.recipient_id=lastmes_out.authr AND m2.time=lastmes_out.maxtime " +

                    "  JOIN "+
                    " users AS us2 "+
                    " ON (m2.author_id=us2.id AND lastmes_in.maxtime IS NOT NULL) " +
                    " OR " +
                    "  (m2.recipient_id=us2.id AND lastmes_out.maxtime IS NOT NULL) "+

                    " LEFT OUTER JOIN "+
                    " persons AS per2 "+
                    " ON (m2.author_id=per2.id AND lastmes_in.maxtime IS NOT NULL) " +
                    " OR " +
                    "  (m2.recipient_id=per2.id AND lastmes_out.maxtime IS NOT NULL) "+


                    " LEFT OUTER JOIN "+
                    "(SELECT COUNT(*) AS unread, author_id AS authr "+
                    "FROM message   "+
                    "WHERE read_status ='SENT' AND recipient_id =:recipientId "+
                    "GROUP BY author_id )  count_only_sent_in "+
                    "ON m2.author_id=count_only_sent_in.authr " +
                    "AND m2.recipient_id =:recipientId  "+

                    " LEFT OUTER JOIN "+
                    "(SELECT COUNT(*) AS unread, recipient_id AS authr "+
                    "FROM message   "+
                    "WHERE read_status ='SENT' AND author_id =:recipientId "+
                    "GROUP BY recipient_id )  count_only_sent_out "+
                    "ON m2.recipient_id=count_only_sent_out.authr  "+
                    "AND m2.author_id =:recipientId  "+
             " ) "+

                    "SELECT  " +
                    " mm.id As XID, mm.time AS XTIME, mm.author_id AS XAID, "+
                    " mm.recipient_id AS XRID, mm.message_text AS XMT, "+
                    " mm.read_status AS XRS, " +
                    " mm.unreadi AS XURI, mm.unreado AS XURO,  "+
                    " mm.first_name AS XFN, mm.photo AS XPH, "+
                    " mm.maxtime_in AS XLONI,  mm.maxtime_out AS XLONO "+
                    " FROM  mm "+
                    "WHERE mm.maxtime_in IS NOT NULL OR mm.maxtime_out IS NOT NULL "+
                    "AND mm.is_deleted = 0 "

            , nativeQuery=true
    )
    List<MessageDBComplexResponce> getOneLastMessageFromEverybodyNativeFull(
            @Param("recipientId") long recipientId,
            Pageable pageable);

}
