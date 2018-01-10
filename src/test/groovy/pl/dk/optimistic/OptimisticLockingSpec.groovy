package pl.dk.optimistic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.orm.ObjectOptimisticLockingFailureException
import org.springframework.test.context.ContextConfiguration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionCallback
import org.springframework.transaction.support.TransactionTemplate
import spock.lang.Specification

@ContextConfiguration(classes = [OptimisticApplication])
@SpringBootTest
class OptimisticLockingSpec extends Specification {

    static final String THE_LAST_WISH = 'The Last Wish'

    @Autowired
    protected PlatformTransactionManager transactionManager

    void doInTx(TransactionCallback<?> callback) {
        TransactionTemplate tt = new TransactionTemplate(transactionManager)
        tt.execute(callback)
    }

    @Autowired
    BookRepository bookRepository

    def setup() {
        bookRepository.deleteAll()
    }

    // no merge is needed, ObjectOptimisticLockingFailureException will be thrown anyway
    def "should throw ObjectOptimisticLockingFailureException with no merge when modified concurrently"() {
        given:
           bookRepository.persist(new Book(THE_LAST_WISH))

        when:
            doInTx({
                Book book = bookRepository.findById(THE_LAST_WISH).get()
                rentedFromOtherThreadBy('Diego')
                book.rentedBy = 'Dawid'
            })

        then:
            thrown(ObjectOptimisticLockingFailureException)
    }

    // of course it will also work with merge
    def "should throw ObjectOptimisticLockingFailureException with merge when modified concurrently"() {
        given:
            bookRepository.persist(new Book(THE_LAST_WISH))

        when:
            doInTx({
                Book book = bookRepository.findById(THE_LAST_WISH).get()
                rentedFromOtherThreadBy('Diego')
                book.rentedBy = 'Dawid'
                bookRepository.save(book) // of course save calls merge if entity already exists - which is the case
            })

        then:
            thrown(ObjectOptimisticLockingFailureException)
    }

    private void rentedFromOtherThreadBy(String rentedBy) {
        Thread otherThread = new Thread({
            doInTx({
                Book book = bookRepository.findById(THE_LAST_WISH).get()
                book.rentedBy = rentedBy
            })
        })
        otherThread.start()
        otherThread.join()
    }

}
