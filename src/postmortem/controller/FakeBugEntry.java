// ═══════════════════════════════════════════════════════════
// POSTMORTEM — Prototype Controllers (Fake Data, No Backend)
// Copy each class into its own file under controller/
// ═══════════════════════════════════════════════════════════

package postmortem.controller;

import postmortem.util.SceneNavigator;
import javafx.fxml.FXML;
import javafx.scene.control.*;



// ── Shared Fake Data Model (prototype only) ───────────────────
public class FakeBugEntry {
    String title, status, tags, created, updated;
    FakeBugEntry(String title, String status, String tags, String created, String updated) {
        this.title   = title;
        this.status  = status;
        this.tags    = tags;
        this.created = created;
        this.updated = updated;
    }
}
