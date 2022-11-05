package com.truncon.web.processes;

import com.truncon.auditing.AuditContext;
import com.truncon.modeling.DbContext;
import com.truncon.modeling.repositories.UserRepository;
import com.truncon.models.User;
import com.truncon.web.ITTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserPreferredNameUpdaterITTest extends ITTest {
    @Test
    public void testUpdatePreferredName() {
        String email = "developer@truncon.com";
        try (DbContext context = getInstance(DbContext.class)) {
            UserRepository userRepository = getInstance(UserRepository.class);
            User user = userRepository.getUserByEmail(email).orElse(null);
            Assertions.assertNotNull(user);

            String preferredName = user.getPreferredName();
            user.setPreferredName(preferredName + "a");
            context.markUpdated(user);

            User updated = userRepository.getUserByEmail(email).orElse(null);
            Assertions.assertNotNull(updated);
            Assertions.assertEquals(preferredName + "a", updated.getPreferredName());

            try (AuditContext.Scope auditScope = AuditContext.getCurrent().runAs(user)) {
                context.saveChanges(); // Performs rollback
            }

            User reverted = userRepository.getUserByEmail(email).orElse(null);
            Assertions.assertNotNull(reverted);
            Assertions.assertEquals(preferredName, reverted.getPreferredName());
        }
    }
}
