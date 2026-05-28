(function () {
    const HISTORY_KEY = "bevisgenerator.revokeHistory.v1";
    const MAX_ITEMS = 100;

    const $ = id => document.getElementById(id);

    function getHistory() {
        try {
            const history = JSON.parse(localStorage.getItem(HISTORY_KEY) || "[]");
            return Array.isArray(history) ? history : [];
        } catch (_err) {
            return [];
        }
    }

    function saveHistory(items) {
        localStorage.setItem(HISTORY_KEY, JSON.stringify(items.slice(0, MAX_ITEMS)));
    }

    function addIssuedEntry(entry) {
        if (!entry.credentialConfigurationId || !entry.issuanceTransactionId) {
            return;
        }

        const issuedAt = entry.issuedAt || new Date().toISOString();
        const history = getHistory().filter(item => item.issuanceTransactionId !== entry.issuanceTransactionId);

        history.unshift({
            issuedAt,
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

        const revokedAt = new Date().toISOString();
        saveHistory(getHistory().map(item => item.issuanceTransactionId === issuanceTransactionId
            ? {...item, status: "revoked", revokedAt}
            : item));
    }

    function formatTime(value) {
        if (!value) {
            return "-";
        }

        const parsed = new Date(value);
        return Number.isNaN(parsed.getTime()) ? value : parsed.toLocaleString("nb-NO");
    }

    function renderRow(item) {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${formatTime(item.issuedAt)}</td>
            <td>
                <div style="display: grid; gap: 0.25rem;">
                    <div>
                        <strong>Credential configuration</strong><br>
                        <code style="display: inline-block; max-width: 100%; overflow-wrap: anywhere; word-break: break-word;">${item.credentialConfigurationId || "-"}</code>
                    </div>
                    <div>
                        <strong>Issuance transaction</strong><br>
                        <code style="display: inline-block; max-width: 100%; overflow-wrap: anywhere; word-break: break-word;">${item.issuanceTransactionId || "-"}</code>
                    </div>
                </div>
            </td>
        `;

        return row;
    }

    function renderHistory() {
        const table = $("recent-issued-table");
        const body = $("recent-issued-body");
        const emptyMessage = $("recent-issued-empty-message");

        if (!table || !body || !emptyMessage) {
            return;
        }

        const history = getHistory();
        body.innerHTML = "";
        table.style.display = history.length ? "table" : "none";
        emptyMessage.style.display = history.length ? "none" : "block";

        if (!history.length) {
            return;
        }

        history.forEach(item => body.appendChild(renderRow(item)));
    }

    function initIssuedMetadataCapture() {
        const metadata = $("issued-metadata");
        if (!metadata) {
            return;
        }

        addIssuedEntry({
            credentialConfigurationId: metadata.dataset.credentialConfigurationId,
            issuanceTransactionId: metadata.dataset.issuanceTransactionId
        });
    }

    function initRevokedSyncFromServer() {
        const state = $("revoke-page-state");
        if (!state) {
            return;
        }

        markRevoked(state.dataset.revokedIssuanceTransactionId);
    }

    window.bevisgeneratorRevokeHistory = {addIssuedEntry, markRevoked, getHistory};

    initIssuedMetadataCapture();
    initRevokedSyncFromServer();
    renderHistory();
})();
