(function () {
    const HISTORY_KEY = "bevisgenerator.revokeHistory.v1";
    const MAX_ITEMS = 100;
    const REVOKE_ENDPOINT = "/revoke/quick";

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
            issuanceTransactionId: entry.issuanceTransactionId
        });

        saveHistory(history);
    }

    function removeIssuedEntry(issuanceTransactionId) {
        if (!issuanceTransactionId) {
            return;
        }

        saveHistory(getHistory().filter(item => item.issuanceTransactionId !== issuanceTransactionId));
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
                <div style="display: grid; gap: 0.4rem;">
                    <div>
                        <strong>Credential configuration</strong><br>
                        <code style="display: inline-block; max-width: 100%; overflow-wrap: anywhere; word-break: break-word;">${item.credentialConfigurationId || "-"}</code>
                    </div>
                    <div>
                        <strong>Issuance transaction</strong><br>
                        <code style="display: inline-block; max-width: 100%; overflow-wrap: anywhere; word-break: break-word;">${item.issuanceTransactionId || "-"}</code>
                    </div>
                    <div>
                        <button class="ds-button" data-size="sm" data-variant="secondary" type="button">
                            Revoker
                        </button>
                    </div>
                </div>
            </td>
        `;

        const button = row.querySelector("button");
        if (button) {
            button.addEventListener("click", () => revokeEntry(item, button));
        }

        return row;
    }

    async function revokeEntry(item, button) {
        const errorMessage = $("recent-issued-error");
        if (!button) {
            return;
        }

        button.disabled = true;
        button.textContent = "Revoker...";

        if (errorMessage) {
            errorMessage.textContent = "";
            errorMessage.style.display = "none";
        }

        try {
            const response = await fetch(REVOKE_ENDPOINT, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                },
                body: new URLSearchParams({
                    credentialConfigurationId: item.credentialConfigurationId || "",
                    issuanceTransactionId: item.issuanceTransactionId || ""
                })
            });

            if (response.status !== 204) {
                throw new Error(`Uventa svar frå revoke-endepunktet: ${response.status}`);
            }

            removeIssuedEntry(item.issuanceTransactionId);
            renderHistory();
        } catch (error) {
            console.error(error);
            if (errorMessage) {
                errorMessage.textContent = "Revokering feila.";
                errorMessage.style.display = "block";
            }
            button.disabled = false;
            button.textContent = "Revoker";
        }
    }

    function initRevokedSyncFromServer() {
        const state = $("revoke-page-state");
        if (!state) {
            return;
        }

        removeIssuedEntry(state.dataset.revokedIssuanceTransactionId);
    }

    function renderHistory() {
        const table = $("recent-issued-table");
        const body = $("recent-issued-body");
        const emptyMessage = $("recent-issued-empty-message");
        const errorMessage = $("recent-issued-error");

        if (!table || !body || !emptyMessage) {
            return;
        }

        const history = getHistory();
        body.innerHTML = "";
        table.style.display = history.length ? "table" : "none";
        emptyMessage.style.display = history.length ? "none" : "block";
        if (errorMessage && !history.length) {
            errorMessage.style.display = "none";
        }

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

    window.bevisgeneratorRevokeHistory = {addIssuedEntry, removeIssuedEntry, getHistory, revokeEntry};

    initIssuedMetadataCapture();
    initRevokedSyncFromServer();
    renderHistory();
})();
