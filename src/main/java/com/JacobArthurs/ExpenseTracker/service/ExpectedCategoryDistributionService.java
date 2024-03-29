package com.JacobArthurs.ExpenseTracker.service;

import com.JacobArthurs.ExpenseTracker.dto.*;
import com.JacobArthurs.ExpenseTracker.enumerator.UserRole;
import com.JacobArthurs.ExpenseTracker.model.Category;
import com.JacobArthurs.ExpenseTracker.model.ExpectedCategoryDistribution;
import com.JacobArthurs.ExpenseTracker.model.User;
import com.JacobArthurs.ExpenseTracker.repository.ExpectedCategoryDistributionRepository;
import com.JacobArthurs.ExpenseTracker.util.OffsetBasedPageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ExpectedCategoryDistributionService {
    private final ExpectedCategoryDistributionRepository expectedCategoryDistributionRepository;
    private final CategoryService categoryService;
    public final CurrentUserProvider currentUserProvider;

    public ExpectedCategoryDistributionService(ExpectedCategoryDistributionRepository expectedCategoryDistribution, CategoryService categoryService, CurrentUserProvider currentUserProvider) {
        this.expectedCategoryDistributionRepository = expectedCategoryDistribution;
        this.categoryService = categoryService;
        this.currentUserProvider = currentUserProvider;
    }

    /**
     * Retrieves all expected category distributions.
     *
     * @return List of expected category distributions
     */
    public List<ExpectedCategoryDistribution> getAllExpectedCategoryDistributions() {
        return expectedCategoryDistributionRepository.findAll();
    }

    /**
     * Retrieves all distributions created by the current user.
     *
     * @return List of distributions
     */
    public List<DistributionDto> getAllDistributions() {
        var sort = Sort.by(Sort.Order.asc("id"));
        var expectedCategoryDistributions = expectedCategoryDistributionRepository.findAllByCreatedBy(currentUserProvider.getCurrentUser(), sort);

        return expectedCategoryDistributions.stream()
                .map(dist -> new DistributionDto(dist.getId(), dist.getCategory().getTitle(), dist.getCategory().getId(), dist.getDistribution()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an expected category distribution by ID after checking authorization.
     *
     * @param id ID of the expected category distribution to retrieve
     * @return The expected category distribution
     * @throws RuntimeException if distribution is not found or user is not authorized
     */
    public ExpectedCategoryDistribution getExpectedCategoryDistributionById(Long id) {
        var expectedCategoryDistribution = expectedCategoryDistributionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expected category distribution not found with ID: " + id));

        if (doesCurrentUserNotOwnExpectedCategoryDistribution(expectedCategoryDistribution))
            throw new RuntimeException("You are not authorized to get an expected category distribution that is not yours.");
        else
            return expectedCategoryDistribution;
    }

    /**
     * Creates an expected category distribution.
     *
     * @param request The expected category distribution request DTO
     * @return Operation result indicating success or failure
     */
    public OperationResult createExpectedCategoryDistribution(ExpectedCategoryDistributionRequestDto request) {
        var expectedCategoryDistribution = new ExpectedCategoryDistribution(request, categoryService.getCategoryById(request.getCategoryId()), currentUserProvider.getCurrentUser());

        expectedCategoryDistributionRepository.save(expectedCategoryDistribution);
        return new OperationResult(true, "Expected category distribution created successfully");
    }

    /**
     * Updates an expected category distribution by ID.
     *
     * @param id      ID of the expected category distribution to update
     * @param request The expected category distribution request DTO
     * @return Operation result indicating success or failure
     */
    public OperationResult updateExpectedCategoryDistribution(Long id, ExpectedCategoryDistributionRequestDto request) {
        var expectedCategoryDistribution = expectedCategoryDistributionRepository.findById(id).orElse(null);

        if (expectedCategoryDistribution == null)
            return new OperationResult(false, "Expected category distribution not found with ID: " + id);
        if (doesCurrentUserNotOwnExpectedCategoryDistribution(expectedCategoryDistribution))
            return new OperationResult(false, "You are not authorized to update an expected category distribution that is not yours.");

        expectedCategoryDistribution.setDistribution(request.getDistribution());
        expectedCategoryDistribution.setLastUpdatedDate(new Timestamp(System.currentTimeMillis()));

        expectedCategoryDistributionRepository.save(expectedCategoryDistribution);
        return new OperationResult(true, "Expected category distribution updated successfully");
    }

    /**
     * Deletes an expected category distribution by ID.
     *
     * @param id ID of the expected category distribution to delete
     * @return Operation result indicating success or failure
     */
    public OperationResult deleteExpectedCategoryDistribution(Long id) {
        var expectedCategoryDistribution = expectedCategoryDistributionRepository.findById(id).orElse(null);

        if (expectedCategoryDistribution == null)
            return new OperationResult(false, "Expected category distribution not found with ID: " + id);
        if (doesCurrentUserNotOwnExpectedCategoryDistribution(expectedCategoryDistribution))
            return new OperationResult(false, "You are not authorized to delete an expected category distribution that is not yours.");

        expectedCategoryDistributionRepository.deleteById(id);
        return new OperationResult(true, "Expected category distribution deleted successfully");
    }

    /**
     * Searches for expected category distributions based on the given criteria.
     *
     * @param request The expected category distribution search request DTO
     * @return Paginated response containing expected category distributions
     */
    public PaginatedResponse<ExpectedCategoryDistribution> searchExpectedCategoryDistributions(ExpectedCategoryDistributionSearchRequestDto request) {
        Specification<ExpectedCategoryDistribution> spec = Specification.where(null);

        spec = spec.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("createdBy"), currentUserProvider.getCurrentUser()));

        if (request.getId() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("id"), request.getId()));
        }

        if (request.getCategoryId() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("category").get("id"), request.getCategoryId()));
        }

        if (request.getStartDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("createdDate"), request.getStartDate()));
        }

        if (request.getEndDate() != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("createdDate"), request.getEndDate()));
        }

        var sort = Sort.by(
                Sort.Order.desc("createdDate"),
                Sort.Order.desc("lastUpdatedDate"),
                Sort.Order.asc("id"));

        Pageable pageable = new OffsetBasedPageRequest(request.getOffset(), request.getLimit(), sort);

        Page<ExpectedCategoryDistribution> categoryPage = expectedCategoryDistributionRepository.findAll(spec, pageable);

        return new PaginatedResponse<>(request.getLimit(), request.getOffset(), categoryPage.getTotalElements(), categoryPage.getContent());
    }

    /**
     * Creates seed data for expected category distributions.
     *
     * @param categories The list of categories
     * @param user       The user to whom categories belong
     */
    public void createSeedData(List<Category> categories, User user) {
        var currentTime = new Timestamp(System.currentTimeMillis());

        var expectedCategoryDistributions = Arrays.asList(
                new ExpectedCategoryDistribution(categories.get(0), 25, currentTime, currentTime, user),  // Housing
                new ExpectedCategoryDistribution(categories.get(1), 15, currentTime, currentTime, user),  // Transportation
                new ExpectedCategoryDistribution(categories.get(2), 15, currentTime, currentTime, user),  // Food
                new ExpectedCategoryDistribution(categories.get(3), 10, currentTime, currentTime, user),  // Utilities
                new ExpectedCategoryDistribution(categories.get(4), 10, currentTime, currentTime, user),  // Insurance
                new ExpectedCategoryDistribution(categories.get(5), 5, currentTime, currentTime, user),   // Medical & Healthcare
                new ExpectedCategoryDistribution(categories.get(6), 5, currentTime, currentTime, user),   // Saving, Investing, & Debt Payments
                new ExpectedCategoryDistribution(categories.get(7), 5, currentTime, currentTime, user),   // Personal Spending
                new ExpectedCategoryDistribution(categories.get(8), 5, currentTime, currentTime, user),   // Recreation & Entertainment
                new ExpectedCategoryDistribution(categories.get(9), 5, currentTime, currentTime, user)    // Miscellaneous
        );

        expectedCategoryDistributionRepository.saveAll(expectedCategoryDistributions);
    }

    /**
     * Checks if the current user does not own the expected category distribution.
     *
     * @param expectedCategoryDistribution The expected category distribution to check
     * @return True if the current user does not own the distribution, false otherwise
     */
    private boolean doesCurrentUserNotOwnExpectedCategoryDistribution(ExpectedCategoryDistribution expectedCategoryDistribution) {
        var currentUser = currentUserProvider.getCurrentUser();
        return !Objects.equals(expectedCategoryDistribution.getCreatedBy().getId(), currentUser.getId()) &&
                !UserRole.ADMIN.equals(currentUser.getRole());
    }
}
