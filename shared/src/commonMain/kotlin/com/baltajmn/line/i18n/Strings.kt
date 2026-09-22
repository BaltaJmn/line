package com.baltajmn.line.i18n

import kotlinx.datetime.LocalDate

/** Two-letter code of the device language. */
expect fun systemLanguage(): String

/** The languages the app ships. Anything else falls back to English. */
internal val SUPPORTED = listOf("en", "es", "pt", "de", "fr")

internal fun normalizeLanguage(code: String): String =
    code.take(2).lowercase().takeIf { it in SUPPORTED } ?: "en"

/**
 * Every user-facing string, in one table, copied from docs/textos.md.
 *
 * Not Compose Resources on purpose: part of these strings are drawn outside a `@Composable` (a
 * BroadcastReceiver, a Glance widget, the Canvas of the share cards, a notification builder).
 *
 * ponytail: the language is read once at first access and the plain texts are resolved then. Both
 * systems restart the app when the language changes, so this only matters if live switching is
 * ever needed. The functions read [lang] on every call, which lets the tests go through the five.
 */
object S {

    internal var lang = normalizeLanguage(systemLanguage())

    private fun t(en: String, es: String, pt: String, de: String, fr: String): String = when (lang) {
        "es" -> es
        "pt" -> pt
        "de" -> de
        "fr" -> fr
        else -> en
    }

    // 1. Dates

    fun monthNames(): List<String> = t(
        "January, February, March, April, May, June, July, August, September, October, November, December",
        "enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre",
        "janeiro, fevereiro, março, abril, maio, junho, julho, agosto, setembro, outubro, novembro, dezembro",
        "Januar, Februar, März, April, Mai, Juni, Juli, August, September, Oktober, November, Dezember",
        "janvier, février, mars, avril, mai, juin, juillet, août, septembre, octobre, novembre, décembre",
    ).split(", ")

    fun monthShort(): List<String> = t(
        "Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec",
        "ene, feb, mar, abr, may, jun, jul, ago, sept, oct, nov, dic",
        "jan, fev, mar, abr, mai, jun, jul, ago, set, out, nov, dez",
        "Jan., Feb., März, Apr., Mai, Juni, Juli, Aug., Sept., Okt., Nov., Dez.",
        "janv., févr., mars, avr., mai, juin, juil., août, sept., oct., nov., déc.",
    ).split(", ")

    fun monthInitials(): List<String> = t(
        "J, F, M, A, M, J, J, A, S, O, N, D",
        "E, F, M, A, M, J, J, A, S, O, N, D",
        "J, F, M, A, M, J, J, A, S, O, N, D",
        "J, F, M, A, M, J, J, A, S, O, N, D",
        "J, F, M, A, M, J, J, A, S, O, N, D",
    ).split(", ")

    fun weekdayNames(): List<String> = t(
        "Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday",
        "lunes, martes, miércoles, jueves, viernes, sábado, domingo",
        "segunda-feira, terça-feira, quarta-feira, quinta-feira, sexta-feira, sábado, domingo",
        "Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag",
        "lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche",
    ).split(", ")

    fun weekdayShort(): List<String> = t(
        "Mon, Tue, Wed, Thu, Fri, Sat, Sun",
        "lun, mar, mié, jue, vie, sáb, dom",
        "seg, ter, qua, qui, sex, sáb, dom",
        "Mo, Di, Mi, Do, Fr, Sa, So",
        "lun, mar, mer, jeu, ven, sam, dim",
    ).split(", ")

    // 2. Today
    val todayPlaceholder = t(
        "Whatever you want to remember about today",
        "Lo que quieras recordar de hoy",
        "O que você quiser lembrar de hoje",
        "Was du dir von heute merken willst",
        "Ce que tu veux retenir d'aujourd'hui",
    )
    val firstHelp = t(
        "One line a day. In a year, on this same day, you'll read it again.",
        "Una línea al día. Dentro de un año, este mismo día, volverás a leerla.",
        "Uma linha por dia. Daqui a um ano, neste mesmo dia, você vai reler.",
        "Eine Zeile am Tag. In einem Jahr, an genau diesem Tag, liest du sie wieder.",
        "Une ligne par jour. Dans un an, ce même jour, tu la reliras.",
    )
    val echoWeek = t(
        "A week ago",
        "Hace una semana",
        "Há uma semana",
        "Vor einer Woche",
        "Il y a une semaine",
    )
    val echoMonth = t("A month ago", "Hace un mes", "Há um mês", "Vor einem Monat", "Il y a un mois")
    val milestoneFirst = t(
        "Your first line. In a year it'll come back.",
        "Tu primera línea. Dentro de un año volverá.",
        "Sua primeira linha. Daqui a um ano ela volta.",
        "Deine erste Zeile. In einem Jahr kommt sie wieder.",
        "Ta première ligne. Dans un an, elle reviendra.",
    )
    val milestoneThirty = t(
        "Your 30th line.",
        "Tu línea número 30.",
        "Sua linha número 30.",
        "Deine 30. Zeile.",
        "Ta 30e ligne.",
    )
    val milestoneHundred = t(
        "One hundred lines.",
        "Cien líneas.",
        "Cem linhas.",
        "Hundert Zeilen.",
        "Cent lignes.",
    )
    val milestoneAnniversary = t(
        "A year ago today, you started this diary.",
        "Hoy hace un año que empezaste este diario.",
        "Hoje faz um ano que você começou este diário.",
        "Heute vor einem Jahr hast du dieses Tagebuch begonnen.",
        "Il y a un an aujourd'hui, tu as commencé ce journal.",
    )
    val milestoneThreeYears = t(
        "Today marks three years on the same page.",
        "Hoy tienes tres años en la misma página.",
        "Hoje você completa três anos na mesma página.",
        "Heute sind es drei Jahre auf derselben Seite.",
        "Aujourd'hui, trois ans sur la même page.",
    )
    val noticeSaveFailed = t(
        "Couldn't save. I'll try again with your next change.",
        "No se ha podido guardar. Lo intento otra vez con tu próximo cambio.",
        "Não foi possível salvar. Vou tentar de novo na sua próxima alteração.",
        "Konnte nicht gespeichert werden. Ich versuche es bei deiner nächsten Änderung erneut.",
        "Impossible d'enregistrer. Je réessaierai avec ta prochaine modification.",
    )
    val noticeCorrupt = t(
        "Couldn't read the diary. The files were saved separately and nothing was deleted.",
        "No se ha podido leer el diario. Los ficheros se han guardado aparte y no se ha borrado nada.",
        "Não foi possível ler o diário. Os arquivos foram guardados à parte e nada foi apagado.",
        "Das Tagebuch konnte nicht gelesen werden. Die Dateien wurden separat gesichert, gelöscht wurde nichts.",
        "Impossible de lire le journal. Les fichiers ont été sauvegardés à part, rien n'a été supprimé.",
    )
    val noticeBackup = t(
        "You've been writing for a month. Save a copy off your phone?",
        "Hace un mes que escribes. ¿Guardas una copia fuera del teléfono?",
        "Já faz um mês que você escreve. Quer guardar uma cópia fora do telefone?",
        "Du schreibst seit einem Monat. Sicherst du eine Kopie außerhalb des Handys?",
        "Ça fait un mois que tu écris. Tu gardes une copie hors du téléphone ?",
    )
    val makeBackup = t("Make a backup", "Hacer copia", "Fazer cópia", "Kopie erstellen", "Faire une copie")

    // 3. Year
    val searchPlaceholder = t(
        "Search your diary",
        "Buscar en el diario",
        "Buscar no diário",
        "Im Tagebuch suchen",
        "Chercher dans le journal",
    )
    val yearEmpty = t(
        "Your year will fill in, line by line.",
        "Tu año se irá llenando línea a línea.",
        "Seu ano vai se preenchendo linha a linha.",
        "Dein Jahr füllt sich Zeile für Zeile.",
        "Ton année se remplira ligne après ligne.",
    )
    val noResults = t(
        "Nothing with those words.",
        "Nada con esas palabras.",
        "Nada com essas palavras.",
        "Nichts mit diesen Wörtern.",
        "Rien avec ces mots.",
    )

    // 4. Open day
    val changePhoto = t("Change photo", "Cambiar foto", "Trocar foto", "Foto ändern", "Changer de photo")
    val removePhoto = t("Remove photo", "Quitar foto", "Remover foto", "Foto entfernen", "Retirer la photo")
    val deleteTitle = t(
        "Delete this day?",
        "¿Borrar este día?",
        "Excluir este dia?",
        "Diesen Tag löschen?",
        "Supprimer ce jour ?",
    )
    val deleteText = t(
        "The line and its photo will be deleted. This cannot be undone.",
        "Se borran la línea y su foto. No se puede deshacer.",
        "A linha e a foto dela serão excluídas. Não é possível desfazer.",
        "Die Zeile und ihr Foto werden gelöscht. Das lässt sich nicht rückgängig machen.",
        "La ligne et sa photo seront supprimées. Cette action est irréversible.",
    )
    val delete = t("Delete", "Borrar", "Excluir", "Löschen", "Supprimer")

    // 5. Settings
    val settingsTitle = t("Settings", "Ajustes", "Ajustes", "Einstellungen", "Réglages")
    val sectionReminder = t("Reminder", "Recordatorio", "Lembrete", "Erinnerung", "Rappel")
    val sectionPrivacy = t("Privacy", "Privacidad", "Privacidade", "Datenschutz", "Confidentialité")
    val sectionCover = t("Cover", "Portada", "Capa", "Umschlag", "Couverture")
    val sectionBackup = t("Backup", "Copia", "Cópia", "Sicherung", "Sauvegarde")
    val sectionPro = t("Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro")
    val sectionMoreApps = t("More apps", "Más apps", "Mais apps", "Weitere Apps", "Plus d'apps")
    val sectionAbout = t("About", "Acerca de", "Sobre", "Über", "À propos")
    val reminderRow = t(
        "Daily reminder",
        "Recordatorio diario",
        "Lembrete diário",
        "Tägliche Erinnerung",
        "Rappel quotidien",
    )
    val reminderOff = t("Off", "Apagado", "Desativado", "Aus", "Désactivé")
    val reminderDenied = t(
        "Purl's notifications are turned off in the system.",
        "Las notificaciones de Purl están desactivadas en el sistema.",
        "As notificações do Purl estão desativadas no sistema.",
        "Die Benachrichtigungen von Purl sind im System deaktiviert.",
        "Les notifications de Purl sont désactivées dans le système.",
    )
    val openSystemSettings = t(
        "Open settings",
        "Abrir ajustes",
        "Abrir ajustes",
        "Einstellungen öffnen",
        "Ouvrir les réglages",
    )
    val lockRow = t(
        "Lock the diary",
        "Bloquear el diario",
        "Bloquear o diário",
        "Tagebuch sperren",
        "Verrouiller le journal",
    )
    val lockSubtitle = t(
        "Asks for your face, your fingerprint or your phone code",
        "Pide tu cara, tu huella o el código del teléfono",
        "Pede seu rosto, sua digital ou o código do telefone",
        "Fragt nach deinem Gesicht, deinem Fingerabdruck oder dem Code des Handys",
        "Demande ton visage, ton empreinte ou le code du téléphone",
    )
    val lockUnavailable = t(
        "Set a screen lock on your phone to use this.",
        "Pon un bloqueo de pantalla en el teléfono para usarlo.",
        "Configure um bloqueio de tela no telefone para usar isso.",
        "Richte eine Bildschirmsperre auf dem Handy ein, um das zu nutzen.",
        "Active un verrouillage d'écran sur ton téléphone pour l'utiliser.",
    )
    val coverProHint = t(
        "Sage is free. The rest come with Purl Pro.",
        "Salvia es gratis; las demás, con Purl Pro.",
        "Sálvia é grátis. As demais vêm com o Purl Pro.",
        "Salbei ist kostenlos. Die anderen mit Purl Pro.",
        "Sauge est gratuite. Les autres sont avec Purl Pro.",
    )
    val exportRow = t(
        "Export backup",
        "Exportar copia",
        "Exportar cópia",
        "Kopie exportieren",
        "Exporter une copie",
    )
    val lastBackupNever = t(
        "No backup yet",
        "Todavía ninguna copia",
        "Ainda nenhuma cópia",
        "Noch keine Sicherung",
        "Encore aucune copie",
    )
    val exportNothing = t(
        "There's nothing to back up yet.",
        "Aún no hay nada que copiar.",
        "Ainda não há nada para copiar.",
        "Es gibt noch nichts zu sichern.",
        "Il n'y a encore rien à copier.",
    )
    val importRow = t(
        "Import backup",
        "Importar copia",
        "Importar cópia",
        "Kopie importieren",
        "Importer une copie",
    )
    val importSubtitle = t(
        "Joins your diary, nothing gets deleted",
        "Se junta con tu diario, sin borrar nada",
        "Se junta ao seu diário, sem apagar nada",
        "Wird mit deinem Tagebuch zusammengeführt, nichts wird gelöscht",
        "Se joint à ton journal, rien n'est supprimé",
    )
    val proRow = t("Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro")
    val proSubtitle = t(
        "Photos, covers and widgets. One-time payment",
        "Fotos, portadas y widgets. Pago único",
        "Fotos, capas e widgets. Pagamento único",
        "Fotos, Umschläge und Widgets. Einmalzahlung",
        "Photos, couvertures et widgets. Paiement unique",
    )
    val proOwned = t(
        "Purchased. Thank you.",
        "Comprado. Gracias.",
        "Comprado. Obrigado.",
        "Gekauft. Danke.",
        "Acheté. Merci.",
    )
    val restoreRow = t(
        "Restore purchase",
        "Restaurar compra",
        "Restaurar compra",
        "Kauf wiederherstellen",
        "Restaurer l'achat",
    )
    val restoreDone = t(
        "Purchase restored.",
        "Compra restaurada.",
        "Compra restaurada.",
        "Kauf wiederhergestellt.",
        "Achat restauré.",
    )
    val restoreNothing = t(
        "There's no purchase to restore.",
        "No hay ninguna compra que restaurar.",
        "Não há nenhuma compra para restaurar.",
        "Es gibt keinen Kauf zum Wiederherstellen.",
        "Il n'y a aucun achat à restaurer.",
    )
    val siblingQuilt = t(
        "Your habits, a year at a glance",
        "Tus hábitos, un año a la vista",
        "Seus hábitos, um ano à vista",
        "Deine Gewohnheiten, ein Jahr im Blick",
        "Tes habitudes, une année en un coup d'oeil",
    )
    val siblingMood = t(
        "How each day went, in colour",
        "Cómo te ha ido cada día, en color",
        "Como foi cada dia, em cores",
        "Wie jeder Tag war, in Farbe",
        "Comment chaque jour s'est passé, en couleur",
    )
    val privacyRow = t(
        "Privacy policy",
        "Política de privacidad",
        "Política de privacidade",
        "Datenschutz",
        "Confidentialité",
    )

    // 6. Lock
    val unlock = t("Unlock", "Desbloquear", "Desbloquear", "Entsperren", "Déverrouiller")
    val lockPromptTitle = t("Open Purl", "Abrir Purl", "Abrir Purl", "Purl öffnen", "Ouvrir Purl")
    val lockPromptSubtitle = t(
        "Your diary is locked",
        "Tu diario está bloqueado",
        "Seu diário está bloqueado",
        "Dein Tagebuch ist gesperrt",
        "Ton journal est verrouillé",
    )

    // 7. Dialogs and notices
    val ok = t("OK", "Vale", "OK", "OK", "OK")
    val cancel = t("Cancel", "Cancelar", "Cancelar", "Abbrechen", "Annuler")
    val yes = t("Yes", "Sí", "Sim", "Ja", "Oui")
    val notNow = t("Not now", "Ahora no", "Agora não", "Jetzt nicht", "Pas maintenant")
    val working = t("One moment...", "Un momento...", "Um momento...", "Einen Moment...", "Un instant...")
    val importTitle = t(
        "Import backup",
        "Importar copia",
        "Importar cópia",
        "Kopie importieren",
        "Importer une copie",
    )
    val importAction = t("Import", "Importar", "Importar", "Importieren", "Importer")
    val importFailedTitle = t(
        "Couldn't import",
        "No se ha podido importar",
        "Não foi possível importar",
        "Import fehlgeschlagen",
        "Échec de l'import",
    )
    val importNotBackup = t(
        "That file is not a backup from Purl.",
        "Ese fichero no es una copia de Purl.",
        "Esse arquivo não é uma cópia do Purl.",
        "Diese Datei ist keine Sicherung von Purl.",
        "Ce fichier n'est pas une copie de Purl.",
    )
    val importDamaged = t(
        "The backup is incomplete or damaged. Your diary wasn't touched.",
        "La copia está incompleta o dañada. Tu diario no se ha tocado.",
        "A cópia está incompleta ou danificada. Seu diário não foi alterado.",
        "Die Sicherung ist unvollständig oder beschädigt. Dein Tagebuch wurde nicht verändert.",
        "La copie est incomplète ou endommagée. Ton journal n'a pas été touché.",
    )
    val importTooNew = t(
        "This backup is from a newer version of Purl. Update the app and try again.",
        "Esta copia es de una versión más nueva de Purl. Actualiza la app y vuelve a probar.",
        "Esta cópia é de uma versão mais nova do Purl. Atualize o app e tente de novo.",
        "Diese Sicherung stammt aus einer neueren Version von Purl. Aktualisiere die App und versuch es erneut.",
        "Cette copie vient d'une version plus récente de Purl. Mets à jour l'app et réessaie.",
    )
    val importEmpty = t(
        "The backup has no lines.",
        "La copia no tiene ninguna línea.",
        "A cópia não tem nenhuma linha.",
        "Die Sicherung enthält keine Zeile.",
        "La copie ne contient aucune ligne.",
    )
    val importIsMoodTraker = t(
        "This is a backup from MoodTraker. You'll be able to bring its notes in the next version.",
        "Es una copia de MoodTraker. Podrás traer sus notas en la próxima versión.",
        "Isso é uma cópia do MoodTraker. Você vai poder trazer as notas dele na próxima versão.",
        "Das ist eine Sicherung von MoodTraker. Du kannst die Notizen in der nächsten Version übernehmen.",
        "C'est une copie de MoodTraker. Tu pourras importer ses notes dans la prochaine version.",
    )
    val exportFailed = t(
        "Couldn't save the backup.",
        "No se ha podido guardar la copia.",
        "Não foi possível salvar a cópia.",
        "Die Sicherung konnte nicht gespeichert werden.",
        "Impossible d'enregistrer la copie.",
    )

    // 8. Purl Pro
    val proTitle = t("Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro", "Purl Pro")
    val proPhotos = t(
        "Photos in every entry",
        "Fotos en todas tus entradas",
        "Fotos em todas as suas entradas",
        "Fotos in jedem Eintrag",
        "Photos dans toutes tes entrées",
    )
    val proCovers = t(
        "Seven more covers",
        "Siete portadas más",
        "Mais sete capas",
        "Sieben weitere Umschläge",
        "Sept couvertures de plus",
    )
    val proYearWidget = t(
        "The year widget",
        "El widget del año",
        "O widget do ano",
        "Das Jahres-Widget",
        "Le widget de l'année",
    )
    val proLockWidget = t(
        "The lock screen widget",
        "El widget de la pantalla de bloqueo",
        "O widget da tela de bloqueio",
        "Das Sperrbildschirm-Widget",
        "Le widget de l'écran verrouillé",
    )
    val proOnce = t(
        "One-time payment, no subscription.",
        "Pago único, sin suscripción.",
        "Pagamento único, sem assinatura.",
        "Einmalzahlung, kein Abo.",
        "Paiement unique, sans abonnement.",
    )
    val restore = t("Restore", "Restaurar", "Restaurar", "Wiederherstellen", "Restaurer")
    val storeUnavailable = t(
        "The store is not available right now.",
        "La tienda no está disponible ahora.",
        "A loja não está disponível agora.",
        "Der Store ist gerade nicht verfügbar.",
        "La boutique n'est pas disponible pour le moment.",
    )
    val buyFailed = t(
        "The purchase could not be completed.",
        "No se ha podido completar la compra.",
        "Não foi possível concluir a compra.",
        "Der Kauf konnte nicht abgeschlossen werden.",
        "L'achat n'a pas pu être finalisé.",
    )

    // 9. Sharing
    val share = t("Share", "Compartir", "Compartilhar", "Teilen", "Partager")
    val saveToPhotos = t(
        "Save to Photos",
        "Guardar en fotos",
        "Salvar nas fotos",
        "In Fotos speichern",
        "Enregistrer la photo",
    )
    val saved = t(
        "Saved to your photos.",
        "Guardada en tus fotos.",
        "Salva nas suas fotos.",
        "In deinen Fotos gespeichert.",
        "Enregistrée dans tes photos.",
    )
    val saveFailed = t(
        "Couldn't save.",
        "No se ha podido guardar.",
        "Não foi possível salvar.",
        "Konnte nicht gespeichert werden.",
        "Impossible d'enregistrer.",
    )
    val cardTagline = t(
        "one line a day",
        "una línea al día",
        "uma linha por dia",
        "eine Zeile am Tag",
        "une ligne par jour",
    )

    // 10. Notification
    val reminderTitle = t(
        "A moment for today's line",
        "Un momento para tu línea de hoy",
        "Um momento para sua linha de hoje",
        "Ein Moment für deine Zeile heute",
        "Un moment pour ta ligne du jour",
    )
    val reminderMemoryTitle = t(
        "A year ago, today",
        "Hace un año, hoy",
        "Há um ano, hoje",
        "Vor einem Jahr, heute",
        "Il y a un an, aujourd'hui",
    )
    val reminderChannel = t(
        "Daily reminder",
        "Recordatorio diario",
        "Lembrete diário",
        "Tägliche Erinnerung",
        "Rappel quotidien",
    )

    // 11. Widgets
    val widgetWritten = t("Written", "Escrita", "Escrita", "Geschrieben", "Écrite")
    val widgetNotWritten = t(
        "Not written yet",
        "Por escribir",
        "Por escrever",
        "Noch nicht geschrieben",
        "À écrire",
    )
    val widgetMemory = t(
        "There's a memory",
        "Hay recuerdo",
        "Há uma lembrança",
        "Es gibt eine Erinnerung",
        "Il y a un souvenir",
    )
    val widgetUnlock = t(
        "Tap to turn it on",
        "Toca para activarlo",
        "Toque para ativar",
        "Tippen zum Aktivieren",
        "Touche pour l'activer",
    )
    val widgetTodayName = t("Today", "Hoy", "Hoje", "Heute", "Aujourd'hui")
    val widgetTodayDescription = t(
        "Whether today has a line and whether there's a memory",
        "Si hoy ya tiene línea y si hay recuerdo",
        "Se hoje já tem linha e se há lembrança",
        "Ob heute schon eine Zeile hat und ob es eine Erinnerung gibt",
        "Si aujourd'hui a déjà une ligne et s'il y a un souvenir",
    )
    val widgetYearName = t("The year", "El año", "O ano", "Das Jahr", "L'année")
    val widgetYearDescription = t(
        "Your year, day by day",
        "Tu año, día a día",
        "Seu ano, dia a dia",
        "Dein Jahr, Tag für Tag",
        "Ton année, jour après jour",
    )

    // 12. Accessibility
    val a11yBack = t("Back", "Volver", "Voltar", "Zurück", "Retour")
    val a11yPreviousYear = t(
        "Previous year",
        "Año anterior",
        "Ano anterior",
        "Vorheriges Jahr",
        "Année précédente",
    )
    val a11yNextYear = t("Next year", "Año siguiente", "Próximo ano", "Nächstes Jahr", "Année suivante")
    val a11yShare = t("Share", "Compartir", "Compartilhar", "Teilen", "Partager")
    val a11ySettings = t("Settings", "Ajustes", "Ajustes", "Einstellungen", "Réglages")
    val a11yYear = t("The year", "El año", "O ano", "Das Jahr", "L'année")
    val a11yClose = t("Close", "Cerrar", "Fechar", "Schließen", "Fermer")
    val a11yPhoto = t("Add photo", "Añadir foto", "Adicionar foto", "Foto hinzufügen", "Ajouter une photo")
    val a11yDelete = t(
        "Delete this day",
        "Borrar este día",
        "Excluir este dia",
        "Diesen Tag löschen",
        "Supprimer ce jour",
    )
    val a11ySelected = t("selected", "elegida", "selecionada", "ausgewählt", "sélectionnée")
    val a11yOpenDay = t(
        "Open this day",
        "Abrir este día",
        "Abrir este dia",
        "Diesen Tag öffnen",
        "Ouvrir ce jour",
    )

    // --- With parameters ----------------------------------------------------------------------
    // Dates are written by hand, never with a platform formatter, so a day reads the same on both
    // systems. Plurals: the singular only for 1.

    fun shortDate(d: LocalDate): String {
        val m = monthNames()[d.month.ordinal]
        return t("$m ${d.day}", "${d.day} de $m", "${d.day} de $m", "${d.day}. $m", "${d.day} $m")
    }

    fun longDate(d: LocalDate): String {
        val w = weekdayNames()[d.dayOfWeek.ordinal].replaceFirstChar { it.uppercase() }
        return w + t(", ", ", ", ", ", ", ", " ") + shortDate(d)
    }

    fun dayMonthYear(d: LocalDate) = shortDate(d) + yearSuffix(d.year)

    fun longDateWithYear(d: LocalDate) = longDate(d) + yearSuffix(d.year)

    fun abbrDateWithYear(d: LocalDate): String {
        val m = monthShort()[d.month.ordinal]
        val y = d.year
        return t("$m ${d.day}, $y", "${d.day} $m $y", "${d.day} $m $y", "${d.day}. $m $y", "${d.day} $m $y")
    }

    fun widgetDate(d: LocalDate): String {
        val w = weekdayShort()[d.dayOfWeek.ordinal]
        val m = monthShort()[d.month.ordinal].removeSuffix(".")
        return "$w ${d.day} $m".uppercase()
    }

    private fun yearSuffix(y: Int) = t(", $y", " de $y", " de $y", " $y", " $y")

    /** Always 24 hours, in the five languages. */
    fun clock(hour: Int, minute: Int) =
        "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"

    fun streakDays(n: Int) =
        t("$n days in a row", "$n días seguidos", "$n dias seguidos", "$n Tage in Folge", "$n jours d'affilée")

    fun counter(n: Int, max: Int) = "$n/$max"

    fun pastYearLabel(year: Int, n: Int): String {
        val ago = if (n == 1) {
            t("a year ago", "hace un año", "há um ano", "vor einem Jahr", "il y a un an")
        } else {
            t("$n years ago", "hace $n años", "há $n anos", "vor $n Jahren", "il y a $n ans")
        }
        return "$year, $ago"
    }

    fun echoLabel(label: String, d: LocalDate) = "$label, ${shortDate(d)}"

    fun dayNumber(n: Int) = t(
        "Day $n of your diary.",
        "Día $n de tu diario.",
        "Dia $n do seu diário.",
        "Tag $n deines Tagebuchs.",
        "Jour $n de ton journal.",
    )

    fun returnsOn(d: LocalDate): String {
        val date = dayMonthYear(d)
        return t(
            "This page will come back on $date.",
            "Esta página volverá el $date.",
            "Esta página vai voltar em $date.",
            "Diese Seite kommt am $date wieder.",
            "Cette page reviendra le $date.",
        )
    }

    fun offerReminder(hour: Int, minute: Int): String {
        val time = clock(hour, minute)
        return t(
            "Remind you every day at $time?",
            "¿Te lo recuerdo cada día a las $time?",
            "Quer que eu lembre você todo dia às $time?",
            "Soll ich dich jeden Tag um $time erinnern?",
            "Je te le rappelle tous les jours à $time ?",
        )
    }

    fun recapYearOffer(year: Int) =
        t("Your $year in Purl", "Tu $year en Purl", "Seu $year no Purl", "Dein $year in Purl", "Ton $year dans Purl")

    fun yearCount(n: Int, year: Int) = if (n == 1) {
        t("1 line in $year", "1 línea en $year", "1 linha em $year", "1 Zeile in $year", "1 ligne en $year")
    } else {
        t("$n lines in $year", "$n líneas en $year", "$n linhas em $year", "$n Zeilen in $year", "$n lignes en $year")
    }

    fun resultsCount(n: Int) = if (n == 1) {
        t("1 result", "1 resultado", "1 resultado", "1 Ergebnis", "1 résultat")
    } else {
        t("$n results", "$n resultados", "$n resultados", "$n Ergebnisse", "$n résultats")
    }

    fun reminderAt(hour: Int, minute: Int): String {
        val time = clock(hour, minute)
        return t("At $time", "A las $time", "Às $time", "Um $time", "À $time")
    }

    /** An unknown id reads as sage, like the cover itself. */
    fun coverName(id: String): String {
        val names = t(
            "pink, peach, butter, sage, mint, sky, periwinkle, lilac",
            "rosa, melocotón, mantequilla, salvia, menta, cielo, pervinca, lila",
            "rosa, pêssego, manteiga, sálvia, hortelã, céu, pervinca, lilás",
            "Rosa, Pfirsich, Butter, Salbei, Minze, Himmel, Periwinkle, Flieder",
            "rose, pêche, beurre, sauge, menthe, ciel, pervenche, lilas",
        ).split(", ")
        return names[COVER_ORDER.indexOf(id).takeIf { it >= 0 } ?: COVER_ORDER.indexOf("sage")]
    }

    private val COVER_ORDER = listOf("rose", "peach", "butter", "sage", "mint", "sky", "periwinkle", "lilac")

    fun lastBackup(d: LocalDate): String {
        val date = abbrDateWithYear(d)
        return t(
            "Last backup: $date",
            "Última copia: $date",
            "Última cópia: $date",
            "Letzte Sicherung: $date",
            "Dernière copie : $date",
        )
    }

    fun version(v: String) = t("Version $v", "Versión $v", "Versão $v", "Version $v", "Version $v")

    /** Parts that are zero are left out. Callers never pass three zeros: that is importEmpty. */
    fun importSummary(added: Int, joined: Int, same: Int): String {
        val parts = listOfNotNull(
            added.takeIf { it > 0 }?.let { n ->
                if (n == 1) {
                    t("1 new day", "1 día nuevo", "1 dia novo", "1 neuen Tag", "1 nouveau jour")
                } else {
                    t("$n new days", "$n días nuevos", "$n dias novos", "$n neue Tage", "$n nouveaux jours")
                }
            },
            joined.takeIf { it > 0 }?.let { n ->
                if (n == 1) {
                    t(
                        "1 that joins with yours",
                        "1 que se junta con los tuyos",
                        "1 que se junta aos seus",
                        "1, der sich mit deinen verbindet",
                        "1 qui se joint aux tiens",
                    )
                } else {
                    t(
                        "$n that join with yours",
                        "$n que se juntan con los tuyos",
                        "$n que se juntam aos seus",
                        "$n, die sich mit deinen verbinden",
                        "$n qui se joignent aux tiens",
                    )
                }
            },
            same.takeIf { it > 0 }?.let { n ->
                if (n == 1) {
                    t("1 that is the same", "1 igual", "1 igual", "1 gleichen", "1 identique")
                } else {
                    t("$n that are the same", "$n iguales", "$n iguais", "$n gleiche", "$n identiques")
                }
            },
        )
        // German closes the relative clause with a comma before "und".
        val and = if (lang == "de" && joined > 0 && same > 0) ", und " else t(" and ", " y ", " e ", " und ", " et ")
        val list = if (parts.size < 2) parts.joinToString() else parts.dropLast(1).joinToString(", ") + and + parts.last()
        return t(
            "The backup brings $list. Nothing gets deleted.",
            "La copia trae $list. No se borra nada.",
            "A cópia traz $list. Nada é apagado.",
            "Die Sicherung bringt $list. Es wird nichts gelöscht.",
            "La copie apporte $list. Rien n'est supprimé.",
        )
    }

    fun importDone(n: Int) = if (n == 1) {
        t(
            "Diary up to date: 1 day updated.",
            "Diario al día: 1 día actualizado.",
            "Diário atualizado: 1 dia atualizado.",
            "Tagebuch aktuell: 1 Tag aktualisiert.",
            "Journal à jour : 1 jour mis à jour.",
        )
    } else {
        t(
            "Diary up to date: $n days updated.",
            "Diario al día: $n días actualizados.",
            "Diário atualizado: $n dias atualizados.",
            "Tagebuch aktuell: $n Tage aktualisiert.",
            "Journal à jour : $n jours mis à jour.",
        )
    }

    fun buy(price: String) =
        t("Buy for $price", "Comprar por $price", "Comprar por $price", "Für $price kaufen", "Acheter pour $price")

    fun cardLines(n: Int) = if (n == 1) {
        t("1 line", "1 línea", "1 linha", "1 Zeile", "1 ligne")
    } else {
        t("$n lines", "$n líneas", "$n linhas", "$n Zeilen", "$n lignes")
    }

    fun cardLongestStreak(n: Int): String {
        val days = if (n == 1) {
            t("1 day", "1 día", "1 dia", "1 Tag", "1 jour")
        } else {
            t("$n days", "$n días", "$n dias", "$n Tage", "$n jours")
        }
        return t(
            "Longest streak: $days",
            "Racha más larga: $days",
            "Sequência mais longa: $days",
            "Längste Serie: $days",
            "Plus longue série : $days",
        )
    }

    fun a11yDay(d: LocalDate, written: Boolean): String {
        val state = if (written) {
            t("written", "escrita", "escrita", "geschrieben", "écrite")
        } else {
            t("not written", "sin escribir", "sem escrever", "nicht geschrieben", "pas écrite")
        }
        return "${shortDate(d)}, $state"
    }
}
