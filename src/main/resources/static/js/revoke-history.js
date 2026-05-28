(function () {
    const HISTORY_KEY = "bevisgenerator.revokeHistory.v1";
    const MAX_ITEMS = 100;

    function getHistory() {
        try {
            const raw = localStorage.getItem(HISTORY_KEY);
            if (!raw) {
                return [];
            }
            const parsed = JSON.parse(raw);
            return Array.isArray(parsed) ? parsed : [];
        } catch (_err) {
            return [];
        }
    }

    function saveHistory(items) {
        localStorage.setItem(HISTORY_KEY, JSON.stringify(items.slice(0, MAX_ITEMS)));
    }

    function deduplicate(items, issuanceTransactionId) {
        return items.filter(item => item.issuanceTransactionId !== issuanceTransactionId);
    }

    function addIssuedEntry(entry) {
        if (!entry.credentialConfigurationId || !entry.issuanceTransactionId) {
            return;
        }
        const now = new Date().toISOString();
        const history = deduplicate(getHistory(), entry.issuanceTransactionId);
        history.unshift({
            issuedAt: entry.issuedAt || now,
            credentialConfigurationId: entry.credentialConfigurationId,
            issuanceTransactionId: entry.issuanceTransactionId,
            status: entry.status || "issued",
            revokedAt: entry.revokedAt || null
        });
        saveHistory(history);
    }

    function markRevoked(issuanceTransactionId) {
        if (!issuanceTransactionId) {
            return;
        }
        const now = new Date().toISOString();
        const updated = getHistory().map(item => {
            if (item.issuanceTransactionId !== issuanceTransactionId) {
                return item;
            }
            return {
                ...item,
                status: "revoked",
                revokedAt: now
            };
        });
        saveHistory(updated);
    }

    function formatTime(value) {
        if (!value) {
            return "-";
        }
        const parsed = new Date(value);
        if (Number.isNaN(parsed.getTime())) {
            return value;
        }
        return parsed.toLocaleString("nb-NO");
    }

    function renderTable() {
        const table = document.getElementById("recent-issued-table");
        const tableBody = document.getElementById("recent-issued-body");
        const emptyMessage = document.getElementById("recent-issued-empty-message");
        const form = document.getElementById("revoke-form");
        const credentialField = document.getElementById("credentialConfigurationId");
        const issuanceField = document.getElementById("issuanceTransactionId");

        if (!table || !tableBody || !emptyMessage) {
            return;
        }

        const history = getHistory();
        tableBody.innerHTML = "";

        if (history.length === 0) {
            table.style.display = "none";
            emptyMessage.style.display = "block";
            return;
        }

        table.style.display = "table";
        emptyMessage.style.display = "none";

        history.forEach(item => {
            const row = document.createElement("tr");
            const isRevoked = item.status === "revoked";
            const statusText = isRevoked && item.revokedAt
                ? `Revokert (${formatTime(item.revokedAt)})`
                : "Utstedt";

            row.innerHTML = `
                <td>${formatTime(item.issuedAt)}</td>
                <td>${item.credentialConfigurationId || "-"}</td>
                <td><code style="display: inline-block; max-width: 14rem; overflow-wrap: anywhere; word-break: break-word;">${item.issuanceTransactionId || "-"}</code></td>
                <td>${statusText}</td>
                <td style="white-space: nowrap;">
                    <button class="ds-button" data-size="sm" data-variant="secondary" type="button" ${isRevoked ? "disabled" : ""}>
                        ${isRevoked ? "Allereie revokert" : "Revoker"}
                    </button>
                </td>
            `;

            const button = row.querySelector("button");
            if (button && !isRevoked && form && credentialField && issuanceField) {
                button.addEventListener("click", () => {
                    credentialField.value = item.credentialConfigurationId || "";
                    issuanceField.value = item.issuanceTransactionId || "";
                    form.submit();
                });
            }

            tableBody.appendChild(row);
        });
    }

    function initIssuedMetadataCapture() {
        const metadata = document.getElementById("issued-metadata");
        if (!metadata) {
            return;
        }
        addIssuedEntry({
            credentialConfigurationId: metadata.dataset.credentialConfigurationId,
            issuanceTransactionId: metadata.dataset.issuanceTransactionId
        });
    }

    function initRevokedSyncFromServer() {
        const state = document.getElementById("revoke-page-state");
        if (!state) {
            return;
        }
        markRevoked(state.dataset.revokedIssuanceTransactionId);
    }

    window.bevisgeneratorRevokeHistory = {
        addIssuedEntry,
        markRevoked,
        getHistory
    };

    initIssuedMetadataCapture();
    initRevokedSyncFromServer();
    renderTable();
})();
