package demo.cashcard.controller;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import demo.cashcard.entity.CashCard;
import demo.cashcard.repository.CashCardRepository;


@RestController
@RequestMapping("/cashcards")
public class CashCardController {

  private final CashCardRepository cashCardRepository;

  private CashCardController(CashCardRepository cashCardRepository) {
    this.cashCardRepository = cashCardRepository;
  }

  @GetMapping("/{requestedId}")
  public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
    // Perfomance
    CashCard cashCard = findCashCard(requestedId, principal);

    if (cashCard != null) {
      return ResponseEntity.ok(cashCard);
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  /**
   * <p>
   * <b> Create </b> Return status code http 201 Create. Set path location response, for loop for id
   * cashcard
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param newCashCardRequest
   * @param ucb
   * @return
   */
  @PostMapping
  private ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest,
      UriComponentsBuilder ucb, Principal principal) {
    CashCard cashCardWithOwner =
        new CashCard(null, newCashCardRequest.amount(), principal.getName());

    CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
    URI locationOfNewCashCard =
        ucb.path("cashcards/{id}").buildAndExpand(savedCashCard.id()).toUri();
    return ResponseEntity.created(locationOfNewCashCard).build();
  }

  // /**
  // * <p> <b> FindAll </b> </p>
  // * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
  // * @return
  // */
  // @GetMapping()
  // private ResponseEntity<Iterable<CashCard>> findAll() {
  // return ResponseEntity.ok(cashCardRepository.findAll());
  // }
  //

  /**
   * <p>
   * <b> finAll sor and order </b> Implementation
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param pageable
   * @return
   */
  @GetMapping
  private ResponseEntity<List<CashCard>> findAll(Pageable pageable, Principal principal) {
    Page<CashCard> page =
        cashCardRepository.findByOwner(principal.getName(), PageRequest.of(pageable.getPageNumber(),
            pageable.getPageSize(), pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))));
    return ResponseEntity.ok(page.getContent());
  }

  /**
   * <p>
   * <b> PUT </b> update
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param requestedId
   * @param cashCardUpdate
   * @return
   */
  @PutMapping("/{requestedId}")
  private ResponseEntity<Void> putCashCard(@PathVariable Long requestedId,
      @RequestBody CashCard cashCardUpdate, Principal principal) {
    // Perfomance
    CashCard cashCard = findCashCard(requestedId, principal);

    if (cashCard != null) {
      CashCard updatedCashCard =
          new CashCard(cashCard.id(), cashCardUpdate.amount(), principal.getName());
      cashCardRepository.save(updatedCashCard);
      return ResponseEntity.noContent().build();
    }
    // just return 204 NO CONTENT for now.
    return ResponseEntity.notFound().build();
  }

  /**
   * <p>
   * <b> Refactor </b> util (duplicate)
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param requestedId
   * @param principal
   * @return
   */
  private CashCard findCashCard(Long requestedId, Principal principal) {
    return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
  }

  /**
   * <p>
   * <b> DELETE </b>
   * </p>
   * 
   * @author <a href="alineumsoft@gmail.com">C. Alegria</a>
   * @param id
   * @return
   */
  @DeleteMapping("/{id}")
  private ResponseEntity<Void> deleteCashCard(@PathVariable Long id, Principal principal) {
    if (cashCardRepository.existsByIdAndOwner(id, principal.getName())) {
      cashCardRepository.deleteById(id); // Add this line
      return ResponseEntity.noContent().build();

    }
    return ResponseEntity.notFound().build();
  }

}
