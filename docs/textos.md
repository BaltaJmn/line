# Textos de Purl

Todos los textos de la app en los cinco idiomas. De aquí salen `i18n/Strings.kt` (tabla `S`, con
`t(en, es, pt, de, fr)` como en las hermanas), el espejo `L` de los widgets de iOS, los
`InfoPlist.strings`, los `AppShortcuts.strings` y `Localizable.strings` del App Intent, y los
`strings.xml` de Android. Las claves son las de `docs/pantallas.md` y `docs/tecnico.md`.

## Reglas de tono

- Tuteo en español, `du` en alemán, `tu` en francés, `você` en portugués de Brasil.
- Frases cortas y sin exclamaciones. El diario no anima ni celebra con ruido: constata.
- El recordatorio nunca reprocha. Prohibido cualquier equivalente de "no has escrito hoy" o "vas a
  perder tu racha".
- Ninguna promesa de salud mental ni de bienestar: ni "cuida tu mente" ni "reduce el estrés".
- Nada en rojo ni en tono de error. Un fallo se cuenta como lo que es y dice qué pasa después.
- Botones: 22 caracteres como máximo en cualquier idioma (claves marcadas con *). Etiquetas `Eyebrow`:
  24 como máximo.
- Francés: espacio normal antes de `?`, `!` y `:`, nunca espacio duro; comillas rectas.
- Alemán: sustantivos en mayúscula.
- Puntuación ASCII salvo la propia de cada idioma (¿ ¡ en español).
- `Purl` no se traduce ni se declina.
- Las horas, en formato de 24 horas en los cinco idiomas (`formatReminder` de la familia).

## Forma en el código

- Texto sin parámetros: `val todayPlaceholder = t("...", "...", "...", "...", "...")`.
- Con parámetros: función, `fun streakDays(n: Int) = when (lang) { ... }`, con el plural resuelto en
  cada idioma (singular solo para 1).
- Fechas: funciones de `Strings.kt` sobre `monthNames`, `monthShort` y `weekdayNames` escritos a mano,
  como `monthNames(lang)` de Quilt. Nada de formateadores de plataforma.
- Las listas de la sección 1 son funciones (`S.monthNames()`) y las horas se pasan como `hour, minute`:
  `S.clock(h, m)` da el `21:00` que usan `offerReminder` y `reminderAt`. Los tests cambian `S.lang` para
  recorrer los cinco idiomas; los textos sin parámetros se fijan una vez al arrancar.

---

## 1. Fechas

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `monthNames` | | January, February, March, April, May, June, July, August, September, October, November, December | enero, febrero, marzo, abril, mayo, junio, julio, agosto, septiembre, octubre, noviembre, diciembre | janeiro, fevereiro, março, abril, maio, junho, julho, agosto, setembro, outubro, novembro, dezembro | Januar, Februar, März, April, Mai, Juni, Juli, August, September, Oktober, November, Dezember | janvier, février, mars, avril, mai, juin, juillet, août, septembre, octobre, novembre, décembre |
| `monthShort` | | Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec | ene, feb, mar, abr, may, jun, jul, ago, sept, oct, nov, dic | jan, fev, mar, abr, mai, jun, jul, ago, set, out, nov, dez | Jan., Feb., März, Apr., Mai, Juni, Juli, Aug., Sept., Okt., Nov., Dez. | janv., févr., mars, avr., mai, juin, juil., août, sept., oct., nov., déc. |
| `monthInitials` | | J, F, M, A, M, J, J, A, S, O, N, D | E, F, M, A, M, J, J, A, S, O, N, D | J, F, M, A, M, J, J, A, S, O, N, D | J, F, M, A, M, J, J, A, S, O, N, D | J, F, M, A, M, J, J, A, S, O, N, D |
| `weekdayNames` | | Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday | lunes, martes, miércoles, jueves, viernes, sábado, domingo | segunda-feira, terça-feira, quarta-feira, quinta-feira, sexta-feira, sábado, domingo | Montag, Dienstag, Mittwoch, Donnerstag, Freitag, Samstag, Sonntag | lundi, mardi, mercredi, jeudi, vendredi, samedi, dimanche |
| `weekdayShort` | | Mon, Tue, Wed, Thu, Fri, Sat, Sun | lun, mar, mié, jue, vie, sáb, dom | seg, ter, qua, qui, sex, sáb, dom | Mo, Di, Mi, Do, Fr, Sa, So | lun, mar, mer, jeu, ven, sam, dim |
| `longDate` | fecha | Saturday, January 17 | Sábado, 17 de enero | Sábado, 17 de janeiro | Samstag, 17. Januar | Samedi 17 janvier |
| `longDateWithYear` | fecha | Tuesday, January 20, 2026 | Martes, 20 de enero de 2026 | Terça-feira, 20 de janeiro de 2026 | Dienstag, 20. Januar 2026 | Mardi 20 janvier 2026 |
| `shortDate` | fecha | January 10 | 10 de enero | 10 de janeiro | 10. Januar | 10 janvier |
| `dayMonthYear` | fecha | January 17, 2027 | 17 de enero de 2027 | 17 de janeiro de 2027 | 17. Januar 2027 | 17 janvier 2027 |
| `abbrDateWithYear` | fecha | Jan 17, 2027 | 17 ene 2027 | 17 jan 2027 | 17. Jan. 2027 | 17 janv. 2027 |
| `widgetDate` | fecha | SAT 17 JAN | SÁB 17 ENE | SÁB 17 JAN | SA 17 JAN | SAM 17 JANV |

## 2. Hoy

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `todayPlaceholder` | | Whatever you want to remember about today | Lo que quieras recordar de hoy | O que você quiser lembrar de hoje | Was du dir von heute merken willst | Ce que tu veux retenir d'aujourd'hui |
| `firstHelp` | | One line a day. In a year, on this same day, you'll read it again. | Una línea al día. Dentro de un año, este mismo día, volverás a leerla. | Uma linha por dia. Daqui a um ano, neste mesmo dia, você vai reler. | Eine Zeile am Tag. In einem Jahr, an genau diesem Tag, liest du sie wieder. | Une ligne par jour. Dans un an, ce même jour, tu la reliras. |
| `streakDays` | n (>= 2) | 5 days in a row | 5 días seguidos | 5 dias seguidos | 5 Tage in Folge | 5 jours d'affilée |
| `counter` | n, max | 263/280 | 263/280 | 263/280 | 263/280 | 263/280 |
| `pastYearLabel` | year, n | 2026, a year ago / 2025, 2 years ago | 2026, hace un año / 2025, hace 2 años | 2026, há um ano / 2025, há 2 anos | 2026, vor einem Jahr / 2025, vor 2 Jahren | 2026, il y a un an / 2025, il y a 2 ans |
| `echoWeek` | | A week ago | Hace una semana | Há uma semana | Vor einer Woche | Il y a une semaine |
| `echoMonth` | | A month ago | Hace un mes | Há um mês | Vor einem Monat | Il y a un mois |
| `echoLabel` | label, fecha | A week ago, January 10 | Hace una semana, 10 de enero | Há uma semana, 10 de janeiro | Vor einer Woche, 10. Januar | Il y a une semaine, 10 janvier |
| `dayNumber` | n | Day 12 of your diary. | Día 12 de tu diario. | Dia 12 do seu diário. | Tag 12 deines Tagebuchs. | Jour 12 de ton journal. |
| `returnsOn` | fecha | This page will come back on January 17, 2028. | Esta página volverá el 17 de enero de 2028. | Esta página vai voltar em 17 de janeiro de 2028. | Diese Seite kommt am 17. Januar 2028 wieder. | Cette page reviendra le 17 janvier 2028. |
| `milestoneFirst` | | Your first line. | Tu primera línea. | Sua primeira linha. | Deine erste Zeile. | Ta première ligne. |
| `milestoneThirty` | | Your 30th line. | Tu línea número 30. | Sua linha número 30. | Deine 30. Zeile. | Ta 30e ligne. |
| `milestoneHundred` | | One hundred lines. | Cien líneas. | Cem linhas. | Hundert Zeilen. | Cent lignes. |
| `milestoneAnniversary` | | A year ago today, you started this diary. | Hoy hace un año que empezaste este diario. | Hoje faz um ano que você começou este diário. | Heute vor einem Jahr hast du dieses Tagebuch begonnen. | Il y a un an aujourd'hui, tu as commencé ce journal. |
| `milestoneThreeYears` | | Today marks three years on the same page. | Hoy tienes tres años en la misma página. | Hoje você completa três anos na mesma página. | Heute sind es drei Jahre auf derselben Seite. | Aujourd'hui, trois ans sur la même page. |
| `noticeSaveFailed` | | Couldn't save. I'll try again with your next change. | No se ha podido guardar. Lo intento otra vez con tu próximo cambio. | Não foi possível salvar. Vou tentar de novo na sua próxima alteração. | Konnte nicht gespeichert werden. Ich versuche es bei deiner nächsten Änderung erneut. | Impossible d'enregistrer. Je réessaierai avec ta prochaine modification. |
| `noticeCorrupt` | | Couldn't read the diary. The files were saved separately and nothing was deleted. | No se ha podido leer el diario. Los ficheros se han guardado aparte y no se ha borrado nada. | Não foi possível ler o diário. Os arquivos foram guardados à parte e nada foi apagado. | Das Tagebuch konnte nicht gelesen werden. Die Dateien wurden separat gesichert, gelöscht wurde nichts. | Impossible de lire le journal. Les fichiers ont été sauvegardés à part, rien n'a été supprimé. |
| `offerReminder` | hora | Remind you every day at 21:00? | ¿Te lo recuerdo cada día a las 21:00? | Quer que eu lembre você todo dia às 21:00? | Soll ich dich jeden Tag um 21:00 erinnern? | Je te le rappelle tous les jours à 21:00 ? |
| `noticeBackup` | | You've been writing for a month. Save a copy off your phone? | Hace un mes que escribes. ¿Guardas una copia fuera del teléfono? | Já faz um mês que você escreve. Quer guardar uma cópia fora do telefone? | Du schreibst seit einem Monat. Sicherst du eine Kopie außerhalb des Handys? | Ça fait un mois que tu écris. Tu gardes une copie hors du téléphone ? |
| `makeBackup`* | | Make a backup | Hacer copia | Fazer cópia | Kopie erstellen | Faire une copie |
| `recapYearOffer` | year | Your 2027 in Purl | Tu 2027 en Purl | Seu 2027 no Purl | Dein 2027 in Purl | Ton 2027 dans Purl |

## 3. Año

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `searchPlaceholder` | | Search your diary | Buscar en el diario | Buscar no diário | Im Tagebuch suchen | Chercher dans le journal |
| `yearCount` | n, year | 1 line in 2027 / 212 lines in 2027 | 1 línea en 2027 / 212 líneas en 2027 | 1 linha em 2027 / 212 linhas em 2027 | 1 Zeile in 2027 / 212 Zeilen in 2027 | 1 ligne en 2027 / 212 lignes en 2027 |
| `resultsCount` | n | 1 result / 12 results | 1 resultado / 12 resultados | 1 resultado / 12 resultados | 1 Ergebnis / 12 Ergebnisse | 1 résultat / 12 résultats |
| `yearEmpty` | | Your year will fill in, line by line. | Tu año se irá llenando línea a línea. | Seu ano vai se preenchendo linha a linha. | Dein Jahr füllt sich Zeile für Zeile. | Ton année se remplira ligne après ligne. |
| `noResults` | | Nothing with those words. | Nada con esas palabras. | Nada com essas palavras. | Nichts mit diesen Wörtern. | Rien avec ces mots. |

## 4. Día abierto

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `changePhoto`* | | Change photo | Cambiar foto | Trocar foto | Foto ändern | Changer de photo |
| `removePhoto`* | | Remove photo | Quitar foto | Remover foto | Foto entfernen | Retirer la photo |
| `deleteTitle` | | Delete this day? | ¿Borrar este día? | Excluir este dia? | Diesen Tag löschen? | Supprimer ce jour ? |
| `deleteText` | | The line and its photo will be deleted. This cannot be undone. | Se borran la línea y su foto. No se puede deshacer. | A linha e a foto dela serão excluídas. Não é possível desfazer. | Die Zeile und ihr Foto werden gelöscht. Das lässt sich nicht rückgängig machen. | La ligne et sa photo seront supprimées. Cette action est irréversible. |
| `delete`* | | Delete | Borrar | Excluir | Löschen | Supprimer |

## 5. Ajustes

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `settingsTitle` | | Settings | Ajustes | Ajustes | Einstellungen | Réglages |
| `sectionReminder` | | Reminder | Recordatorio | Lembrete | Erinnerung | Rappel |
| `sectionPrivacy` | | Privacy | Privacidad | Privacidade | Datenschutz | Confidentialité |
| `sectionCover` | | Cover | Portada | Capa | Umschlag | Couverture |
| `sectionBackup` | | Backup | Copia | Cópia | Sicherung | Sauvegarde |
| `sectionPro` | | Purl Pro | Purl Pro | Purl Pro | Purl Pro | Purl Pro |
| `sectionMoreApps` | | More apps | Más apps | Mais apps | Weitere Apps | Plus d'apps |
| `sectionAbout` | | About | Acerca de | Sobre | Über | À propos |
| `reminderRow` | | Daily reminder | Recordatorio diario | Lembrete diário | Tägliche Erinnerung | Rappel quotidien |
| `reminderOff` | | Off | Apagado | Desativado | Aus | Désactivé |
| `reminderAt` | hora | At 21:00 | A las 21:00 | Às 21:00 | Um 21:00 | À 21:00 |
| `reminderDenied` | | Purl's notifications are turned off in the system. | Las notificaciones de Purl están desactivadas en el sistema. | As notificações do Purl estão desativadas no sistema. | Die Benachrichtigungen von Purl sind im System deaktiviert. | Les notifications de Purl sont désactivées dans le système. |
| `openSystemSettings`* | | Open settings | Abrir ajustes | Abrir ajustes | Einstellungen öffnen | Ouvrir les réglages |
| `lockRow` | | Lock the diary | Bloquear el diario | Bloquear o diário | Tagebuch sperren | Verrouiller le journal |
| `lockSubtitle` | | Asks for your face, your fingerprint or your phone code | Pide tu cara, tu huella o el código del teléfono | Pede seu rosto, sua digital ou o código do telefone | Fragt nach deinem Gesicht, deinem Fingerabdruck oder dem Code des Handys | Demande ton visage, ton empreinte ou le code du téléphone |
| `lockUnavailable` | | Set a screen lock on your phone to use this. | Pon un bloqueo de pantalla en el teléfono para usarlo. | Configure um bloqueio de tela no telefone para usar isso. | Richte eine Bildschirmsperre auf dem Handy ein, um das zu nutzen. | Active un verrouillage d'écran sur ton téléphone pour l'utiliser. |
| `coverProHint` | | Sage is free. The rest come with Purl Pro. | Salvia es gratis; las demás, con Purl Pro. | Sálvia é grátis. As demais vêm com o Purl Pro. | Salbei ist kostenlos. Die anderen mit Purl Pro. | Sauge est gratuite. Les autres sont avec Purl Pro. |
| `coverName` | id | pink, peach, butter, sage, mint, sky, periwinkle, lilac | rosa, melocotón, mantequilla, salvia, menta, cielo, pervinca, lila (en el orden de `docs/tecnico.md` sección 5) | rosa, pêssego, manteiga, sálvia, hortelã, céu, pervinca, lilás | Rosa, Pfirsich, Butter, Salbei, Minze, Himmel, Periwinkle, Flieder | rose, pêche, beurre, sauge, menthe, ciel, pervenche, lilas |
| `exportRow` | | Export backup | Exportar copia | Exportar cópia | Kopie exportieren | Exporter une copie |
| `lastBackup` | fecha | Last backup: Jan 17, 2027 | Última copia: 17 ene 2027 | Última cópia: 17 jan 2027 | Letzte Sicherung: 17. Jan. 2027 | Dernière copie : 17 janv. 2027 |
| `lastBackupNever` | | No backup yet | Todavía ninguna copia | Ainda nenhuma cópia | Noch keine Sicherung | Encore aucune copie |
| `exportNothing` | | There's nothing to back up yet. | Aún no hay nada que copiar. | Ainda não há nada para copiar. | Es gibt noch nichts zu sichern. | Il n'y a encore rien à copier. |
| `importRow` | | Import backup | Importar copia | Importar cópia | Kopie importieren | Importer une copie |
| `importSubtitle` | | Joins your diary, nothing gets deleted | Se junta con tu diario, sin borrar nada | Se junta ao seu diário, sem apagar nada | Wird mit deinem Tagebuch zusammengeführt, nichts wird gelöscht | Se joint à ton journal, rien n'est supprimé |
| `proRow` | | Purl Pro | Purl Pro | Purl Pro | Purl Pro | Purl Pro |
| `proSubtitle` | | Photos, covers and widgets. One-time payment | Fotos, portadas y widgets. Pago único | Fotos, capas e widgets. Pagamento único | Fotos, Umschläge und Widgets. Einmalzahlung | Photos, couvertures et widgets. Paiement unique |
| `proOwned` | | Purchased. Thank you. | Comprado. Gracias. | Comprado. Obrigado. | Gekauft. Danke. | Acheté. Merci. |
| `restoreRow` | | Restore purchase | Restaurar compra | Restaurar compra | Kauf wiederherstellen | Restaurer l'achat |
| `restoreDone` | | Purchase restored. | Compra restaurada. | Compra restaurada. | Kauf wiederhergestellt. | Achat restauré. |
| `restoreNothing` | | There's no purchase to restore. | No hay ninguna compra que restaurar. | Não há nenhuma compra para restaurar. | Es gibt keinen Kauf zum Wiederherstellen. | Il n'y a aucun achat à restaurer. |
| `siblingQuilt` | | Your habits, a year at a glance | Tus hábitos, un año a la vista | Seus hábitos, um ano à vista | Deine Gewohnheiten, ein Jahr im Blick | Tes habitudes, une année en un coup d'oeil |
| `siblingMood` | | How each day went, in colour | Cómo te ha ido cada día, en color | Como foi cada dia, em cores | Wie jeder Tag war, in Farbe | Comment chaque jour s'est passé, en couleur |
| `privacyRow` | | Privacy policy | Política de privacidad | Política de privacidade | Datenschutz | Confidentialité |
| `version` | v | Version 1.0 | Versión 1.0 | Versão 1.0 | Version 1.0 | Version 1.0 |

## 6. Bloqueo

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `unlock`* | | Unlock | Desbloquear | Desbloquear | Entsperren | Déverrouiller |
| `lockPromptTitle` | | Open Purl | Abrir Purl | Abrir Purl | Purl öffnen | Ouvrir Purl |
| `lockPromptSubtitle` | | Your diary is locked | Tu diario está bloqueado | Seu diário está bloqueado | Dein Tagebuch ist gesperrt | Ton journal est verrouillé |

## 7. Diálogos y avisos comunes

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `ok`* | | OK | Vale | OK | OK | OK |
| `cancel`* | | Cancel | Cancelar | Cancelar | Abbrechen | Annuler |
| `yes`* | | Yes | Sí | Sim | Ja | Oui |
| `notNow`* | | Not now | Ahora no | Agora não | Jetzt nicht | Pas maintenant |
| `working` | | One moment... | Un momento... | Um momento... | Einen Moment... | Un instant... |
| `importTitle` | | Import backup | Importar copia | Importar cópia | Kopie importieren | Importer une copie |
| `importSummary` | added, joined, same | The backup brings 12 new days, 3 that join with yours and 40 that are the same. Nothing gets deleted. | La copia trae 12 días nuevos, 3 que se juntan con los tuyos y 40 iguales. No se borra nada. | A cópia traz 12 dias novos, 3 que se juntam aos seus e 40 iguais. Nada é apagado. | Die Sicherung bringt 12 neue Tage, 3, die sich mit deinen verbinden, und 40 gleiche. Es wird nichts gelöscht. | La copie apporte 12 nouveaux jours, 3 qui se joignent aux tiens et 40 identiques. Rien n'est supprimé. |
| `importAction`* | | Import | Importar | Importar | Importieren | Importer |
| `importDone` | n | Diary up to date: 15 days updated. | Diario al día: 15 días actualizados. | Diário atualizado: 15 dias atualizados. | Tagebuch aktuell: 15 Tage aktualisiert. | Journal à jour : 15 jours mis à jour. |
| `importDone` | 0 | Diary up to date: there was nothing to change. | Diario al día: no había nada que cambiar. | Diário atualizado: não havia nada para mudar. | Tagebuch aktuell: es gab nichts zu ändern. | Journal à jour : il n'y avait rien à changer. |
| `importFailedTitle` | | Couldn't import | No se ha podido importar | Não foi possível importar | Import fehlgeschlagen | Échec de l'import |
| `importNotBackup` | | That file is not a backup from Purl. | Ese fichero no es una copia de Purl. | Esse arquivo não é uma cópia do Purl. | Diese Datei ist keine Sicherung von Purl. | Ce fichier n'est pas une copie de Purl. |
| `importDamaged` | | The backup is incomplete or damaged. Your diary wasn't touched. | La copia está incompleta o dañada. Tu diario no se ha tocado. | A cópia está incompleta ou danificada. Seu diário não foi alterado. | Die Sicherung ist unvollständig oder beschädigt. Dein Tagebuch wurde nicht verändert. | La copie est incomplète ou endommagée. Ton journal n'a pas été touché. |
| `importTooNew` | | This backup is from a newer version of Purl. Update the app and try again. | Esta copia es de una versión más nueva de Purl. Actualiza la app y vuelve a probar. | Esta cópia é de uma versão mais nova do Purl. Atualize o app e tente de novo. | Diese Sicherung stammt aus einer neueren Version von Purl. Aktualisiere die App und versuch es erneut. | Cette copie vient d'une version plus récente de Purl. Mets à jour l'app et réessaie. |
| `importEmpty` | | The backup has no lines. | La copia no tiene ninguna línea. | A cópia não tem nenhuma linha. | Die Sicherung enthält keine Zeile. | La copie ne contient aucune ligne. |
| `importIsMoodTraker` | | This is a backup from MoodTraker. You'll be able to bring its notes in the next version. | Es una copia de MoodTraker. Podrás traer sus notas en la próxima versión. | Isso é uma cópia do MoodTraker. Você vai poder trazer as notas dele na próxima versão. | Das ist eine Sicherung von MoodTraker. Du kannst die Notizen in der nächsten Version übernehmen. | C'est une copie de MoodTraker. Tu pourras importer ses notes dans la prochaine version. |
| `exportFailed` | | Couldn't save the backup. | No se ha podido guardar la copia. | Não foi possível salvar a cópia. | Die Sicherung konnte nicht gespeichert werden. | Impossible d'enregistrer la copie. |

`importSummary` con plurales: "1 día nuevo", "1 que se junta", "1 igual"; y los ceros se omiten de la
frase (si `joined` es 0, no aparece esa parte). `importDone` cuenta `added + joined`, y tiene su propia
frase cuando sale 0: importar la copia que ya tienes no actualiza nada, y decir "0 días" suena a fallo.

## 8. Purl Pro

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `proTitle` | | Purl Pro | Purl Pro | Purl Pro | Purl Pro | Purl Pro |
| `proPhotos` | | Photos in every entry | Fotos en todas tus entradas | Fotos em todas as suas entradas | Fotos in jedem Eintrag | Photos dans toutes tes entrées |
| `proCovers` | | Seven more covers | Siete portadas más | Mais sete capas | Sieben weitere Umschläge | Sept couvertures de plus |
| `proYearWidget` | | The year widget | El widget del año | O widget do ano | Das Jahres-Widget | Le widget de l'année |
| `proLockWidget` | | The lock screen widget | El widget de la pantalla de bloqueo | O widget da tela de bloqueio | Das Sperrbildschirm-Widget | Le widget de l'écran verrouillé |
| `proOnce` | | One-time payment, no subscription. | Pago único, sin suscripción. | Pagamento único, sem assinatura. | Einmalzahlung, kein Abo. | Paiement unique, sans abonnement. |
| `restore`* | | Restore | Restaurar | Restaurar | Wiederherstellen | Restaurer |
| `buy`* | price | Buy for 5.99 EUR | Comprar por 5,99 EUR | Comprar por 5,99 EUR | Für 5,99 EUR kaufen | Acheter pour 5,99 EUR |
| `storeUnavailable` | | The store is not available right now. | La tienda no está disponible ahora. | A loja não está disponível agora. | Der Store ist gerade nicht verfügbar. | La boutique n'est pas disponible pour le moment. |
| `buyFailed` | | The purchase could not be completed. | No se ha podido completar la compra. | Não foi possível concluir a compra. | Der Kauf konnte nicht abgeschlossen werden. | L'achat n'a pas pu être finalisé. |

## 9. Compartir

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `share`* | | Share | Compartir | Compartilhar | Teilen | Partager |
| `saveToPhotos`* | | Save to Photos | Guardar en fotos | Salvar nas fotos | In Fotos speichern | Enregistrer la photo |
| `saved` | | Saved to your photos. | Guardada en tus fotos. | Salva nas suas fotos. | In deinen Fotos gespeichert. | Enregistrée dans tes photos. |
| `saveFailed` | | Couldn't save. | No se ha podido guardar. | Não foi possível salvar. | Konnte nicht gespeichert werden. | Impossible d'enregistrer. |
| `cardLines` | n | 1 line / 212 lines | 1 línea / 212 líneas | 1 linha / 212 linhas | 1 Zeile / 212 Zeilen | 1 ligne / 212 lignes |
| `cardLongestStreak` | n | Longest streak: 34 days | Racha más larga: 34 días | Sequência mais longa: 34 dias | Längste Serie: 34 Tage | Plus longue série : 34 jours |
| `cardTagline` | | one line a day | una línea al día | uma linha por dia | eine Zeile am Tag | une ligne par jour |

## 10. Notificación

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `reminderTitle` | | A moment for today's line | Un momento para tu línea de hoy | Um momento para sua linha de hoje | Ein Moment für deine Zeile heute | Un moment pour ta ligne du jour |
| `reminderMemoryTitle` | | A year ago, today | Hace un año, hoy | Há um ano, hoje | Vor einem Jahr, heute | Il y a un an, aujourd'hui |
| `reminderChannel` | | Daily reminder | Recordatorio diario | Lembrete diário | Tägliche Erinnerung | Rappel quotidien |

## 11. Widgets

Van en `Strings.kt` (Glance) y en el `enum L` de `LineWidget.swift` (WidgetKit), con los mismos
textos.

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `widgetDate` | fecha | SAT 17 JAN | SÁB 17 ENE (sección 1) | SÁB 17 JAN | SA 17 JAN | SAM 17 JANV |
| `widgetWritten` | | Written | Escrita | Escrita | Geschrieben | Écrite |
| `widgetNotWritten` | | Not written yet | Por escribir | Por escrever | Noch nicht geschrieben | À écrire |
| `widgetMemory` | | There's a memory | Hay recuerdo | Há uma lembrança | Es gibt eine Erinnerung | Il y a un souvenir |
| `widgetUnlock` | | Tap to turn it on | Toca para activarlo | Toque para ativar | Tippen zum Aktivieren | Touche pour l'activer |
| `widgetTodayName` | | Today | Hoy | Hoje | Heute | Aujourd'hui |
| `widgetTodayDescription` | | Whether today has a line and whether there's a memory | Si hoy ya tiene línea y si hay recuerdo | Se hoje já tem linha e se há lembrança | Ob heute schon eine Zeile hat und ob es eine Erinnerung gibt | Si aujourd'hui a déjà une ligne et s'il y a un souvenir |
| `widgetYearName` | | The year | El año | O ano | Das Jahr | L'année |
| `widgetYearDescription` | | Your year, day by day | Tu año, día a día | Seu ano, dia a dia | Dein Jahr, Tag für Tag | Ton année, jour après jour |

Los nombres y descripciones que ve el selector de widgets: en Android, `strings.xml` de cada idioma
(`widget_today_description`, `widget_year_description`); en iOS, `configurationDisplayName` y
`description` son literales en inglés y se traducen en `Localizable.strings` de la extensión.

## 12. Accesibilidad

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `a11yBack` | | Back | Volver | Voltar | Zurück | Retour |
| `a11yPreviousYear` | | Previous year | Año anterior | Ano anterior | Vorheriges Jahr | Année précédente |
| `a11yNextYear` | | Next year | Año siguiente | Próximo ano | Nächstes Jahr | Année suivante |
| `a11yShare` | | Share | Compartir | Compartilhar | Teilen | Partager |
| `a11ySettings` | | Settings | Ajustes | Ajustes | Einstellungen | Réglages |
| `a11yYear` | | The year | El año | O ano | Das Jahr | L'année |
| `a11yClose` | | Close | Cerrar | Fechar | Schließen | Fermer |
| `a11yPhoto` | | Add photo | Añadir foto | Adicionar foto | Foto hinzufügen | Ajouter une photo |
| `a11yDelete` | | Delete this day | Borrar este día | Excluir este dia | Diesen Tag löschen | Supprimer ce jour |
| `a11ySelected` | | selected | elegida | selecionada | ausgewählt | sélectionnée |
| `a11yDay` | fecha, escrita | January 17, written / January 17, not written | 17 de enero, escrita / 17 de enero, sin escribir | 17 de janeiro, escrita / 17 de janeiro, sem escrever | 17. Januar, geschrieben / 17. Januar, nicht geschrieben | 17 janvier, écrite / 17 janvier, pas écrite |
| `a11yOpenDay` | | Open this day | Abrir este día | Abrir este dia | Diesen Tag öffnen | Ouvrir ce jour |

## 13. iOS: `InfoPlist.strings`

| Clave | en | es | pt | de | fr |
|---|---|---|---|---|---|
| `CFBundleDisplayName` | Purl | Purl | Purl | Purl | Purl |
| `NSFaceIDUsageDescription` | To open your diary with Face ID when the lock is on. | Para abrir tu diario con Face ID cuando el bloqueo está puesto. | Para abrir seu diário com Face ID quando o bloqueio estiver ativado. | Um dein Tagebuch mit Face ID zu öffnen, wenn die Sperre aktiv ist. | Pour ouvrir ton journal avec Face ID quand le verrouillage est actif. |
| `NSPhotoLibraryAddUsageDescription` | To save the card you want to share to your camera roll. | Para guardar en tu carrete la tarjeta que quieras compartir. | Para salvar no seu rolo de câmera o cartão que você quiser compartilhar. | Um die Karte, die du teilen möchtest, in deiner Fotomediathek zu speichern. | Pour enregistrer dans ta pellicule la carte que tu veux partager. |

## 14. Android: `strings.xml`

| Clave | en | es | pt | de | fr |
|---|---|---|---|---|---|
| `app_name` | Purl | Purl | Purl | Purl | Purl |
| `widget_today_description` | Whether today has a line and whether there's a memory | Si hoy ya tiene línea y si hay recuerdo | Se hoje já tem linha e se há lembrança | Ob heute schon eine Zeile hat und ob es eine Erinnerung gibt | Si aujourd'hui a déjà une ligne et s'il y a un souvenir |
| `widget_year_description` | Your year, day by day | Tu año, día a día | Seu ano, dia a dia | Dein Jahr, Tag für Tag | Ton année, jour après jour |
| `widget_preview_date` | SAT 17 JAN | SÁB 17 ENE | SÁB 17 JAN | SA 17 JAN | SAM 17 JANV |
| `widget_preview_state` | Not written yet | Por escribir | Por escrever | Noch nicht geschrieben | À écrire |
| `widget_memory_description` (v1.1) | What you wrote a year ago | Lo que escribiste hace un año | O que você escreveu há um ano | Was du vor einem Jahr geschrieben hast | Ce que tu as écrit il y a un an |

---

## 15. v1.1

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `proBook` | | The book, laid out as a PDF | El libro en PDF, maquetado | O livro em PDF, diagramado | Das Buch als gestaltetes PDF | Le livre en PDF, mis en page |
| `proMemoryWidget` | | The memory widget | El widget del recuerdo | O widget da lembrança | Das Erinnerungs-Widget | Le widget du souvenir |
| `proSiri` | | Dictate the line with Siri | Dictar la línea con Siri | Ditar a linha com a Siri | Die Zeile mit Siri diktieren | Dicter la ligne avec Siri |
| `proTile` | | Access from Quick Settings | El acceso desde Ajustes rápidos | O acesso pelas Configurações rápidas | Der Zugriff über die Schnelleinstellungen | L'accès depuis les réglages rapides |
| `bookRow` | | Book as PDF | Libro en PDF | Livro em PDF | Buch als PDF | Livre en PDF |
| `bookSubtitle` | | Your diary laid out, one day per page | Tu diario maquetado, un día por página | Seu diário diagramado, um dia por página | Dein Tagebuch gestaltet, ein Tag pro Seite | Ton journal mis en page, un jour par page |
| `bookYears` | from, to | 2026 to 2030 | 2026 a 2030 | 2026 a 2030 | 2026 bis 2030 | 2026 à 2030 |
| `bookMaking` | n, total | Laying out the book: 120 of 366 | Maquetando el libro: 120 de 366 | Diagramando o livro: 120 de 366 | Buch wird erstellt: 120 von 366 | Mise en page du livre : 120 sur 366 |
| `bookFailed` | | Couldn't create the book. | No se ha podido crear el libro. | Não foi possível criar o livro. | Das Buch konnte nicht erstellt werden. | Impossible de créer le livre. |
| `importMoodRow` | | Import from MoodTraker | Importar de MoodTraker | Importar do MoodTraker | Von MoodTraker importieren | Importer depuis MoodTraker |
| `importMoodCount` | added, skipped | 120 notes come in from MoodTraker. 8 days already had a line and stay as they are. | Entran 120 notas de MoodTraker. 8 días ya tenían línea y se quedan como están. | Entram 120 notas do MoodTraker. 8 dias já tinham linha e ficam como estão. | 120 Notizen aus MoodTraker kommen dazu. 8 Tage hatten schon eine Zeile und bleiben, wie sie sind. | 120 notes de MoodTraker sont importées. 8 jours avaient déjà une ligne et restent tels quels. |
| `importMoodNotBackup` | | That file is not a backup from MoodTraker. | Ese fichero no es una copia de MoodTraker. | Esse arquivo não é uma cópia do MoodTraker. | Diese Datei ist keine Sicherung von MoodTraker. | Ce fichier n'est pas une copie de MoodTraker. |
| `sectionWriting` | | Writing | Escritura | Escrita | Schreiben | Écriture |
| `questionsRow` | | A question when the day is blank | Una pregunta cuando el día está en blanco | Uma pergunta quando o dia está em branco | Eine Frage, wenn der Tag leer ist | Une question quand la page est vide |
| `addTag`* | | + tag | + etiqueta | + etiqueta | + Etikett | + étiquette |
| `tagsLabel` | | Tags | Etiquetas | Etiquetas | Etiketten | Étiquettes |
| `widgetMemoryName` | | Memory | Recuerdo | Lembrança | Erinnerung | Souvenir |
| `widgetMemoryDescription` | | What you wrote a year ago | Lo que escribiste hace un año | O que você escreveu há um ano | Was du vor einem Jahr geschrieben hast | Ce que tu as écrit il y a un an |
| `widgetMemoryLabel` | year | A year ago, 2026 | Hace un año, 2026 | Há um ano, 2026 | Vor einem Jahr, 2026 | Il y a un an, 2026 |
| `widgetNoMemory` | | There's no memory from a year ago today. | Hoy no hay recuerdo de hace un año. | Hoje não há lembrança de um ano atrás. | Heute gibt es keine Erinnerung von vor einem Jahr. | Aujourd'hui, il n'y a pas de souvenir d'il y a un an. |
| `widgetLocked` | | Diary locked. | Diario bloqueado. | Diário bloqueado. | Tagebuch gesperrt. | Journal verrouillé. |
| `tileLabel` | | Today's line | Línea de hoy | Linha de hoje | Heutige Zeile | Ligne du jour |
| `tileWritten` | | Written | Escrita | Escrita | Geschrieben | Écrite |
| `tileNotWritten` | | Not written yet | Por escribir | Por escrever | Noch nicht geschrieben | À écrire |

### Siri y Atajos (iOS)

El código Swift usa los literales en inglés; las traducciones van en `<lang>.lproj/Localizable.strings`
(título, descripción, parámetro, diálogos) y `<lang>.lproj/AppShortcuts.strings` (frases), con la clave
igual al literal inglés.

| Clave (literal inglés) | es | pt | de | fr |
|---|---|---|---|---|
| `Write today's line` (título) | Escribir la línea de hoy | Escrever a linha de hoje | Die heutige Zeile schreiben | Écrire la ligne du jour |
| `Adds a line to today in your diary.` (descripción) | Añade una línea al día de hoy en tu diario. | Adiciona uma linha ao dia de hoje no seu diário. | Fügt eine Zeile zum heutigen Tag in deinem Tagebuch hinzu. | Ajoute une ligne à la journée d'aujourd'hui dans ton journal. |
| `Line` (parámetro) | Línea | Linha | Zeile | Ligne |
| `What do you want to write?` (petición del parámetro) | ¿Qué quieres escribir? | O que você quer escrever? | Was möchtest du schreiben? | Qu'est-ce que tu veux écrire ? |
| `Saved in your diary.` (`siriSaved`) | Guardado en tu diario. | Guardado no seu diário. | In deinem Tagebuch gespeichert. | Enregistré dans ton journal. |
| `Dictating a line is part of Purl Pro. Open the app to see it.` (`siriNeedsPro`) | Dictar una línea es de Purl Pro. Abre la app para verlo. | Ditar uma linha é do Purl Pro. Abra o app para ver. | Eine Zeile diktieren gehört zu Purl Pro. Öffne die App, um es zu sehen. | Dicter une ligne fait partie de Purl Pro. Ouvre l'app pour le voir. |
| `Write my line in ${applicationName}` (frase) | Escribe mi línea en ${applicationName} | Escreva minha linha no ${applicationName} | Schreibe meine Zeile in ${applicationName} | Écris ma ligne dans ${applicationName} |
| `Add a line to ${applicationName}` (frase) | Añade una línea en ${applicationName} | Adicione uma linha ao ${applicationName} | Füge eine Zeile zu ${applicationName} hinzu | Ajoute une ligne à ${applicationName} |

### Banco de preguntas del día

Sesenta, en este orden (el índice es `toEpochDays() mod 60`). Breves, concretas, sobre el día vivido;
ninguna presupone algo triste ni habla de salud mental.

| # | en | es | pt | de | fr |
|---|---|---|---|---|---|
| 1 | What did you eat today that you liked? | ¿Qué has comido hoy que te haya gustado? | O que você comeu hoje que gostou? | Was hast du heute gegessen, das dir geschmeckt hat? | Qu'as-tu mangé aujourd'hui qui t'a plu ? |
| 2 | Who did you talk to today? | ¿Con quién has hablado hoy? | Com quem você falou hoje? | Mit wem hast du heute gesprochen? | À qui as-tu parlé aujourd'hui ? |
| 3 | What made you laugh? | ¿Qué te ha hecho reír? | O que te fez rir? | Was hat dich zum Lachen gebracht? | Qu'est-ce qui t'a fait rire ? |
| 4 | What did you learn today? | ¿Qué has aprendido hoy? | O que você aprendeu hoje? | Was hast du heute gelernt? | Qu'as-tu appris aujourd'hui ? |
| 5 | Where were you today? | ¿Dónde has estado hoy? | Onde você esteve hoje? | Wo warst du heute? | Où as-tu été aujourd'hui ? |
| 6 | What was the best part of the morning? | ¿Qué ha sido lo mejor de la mañana? | Qual foi a melhor parte da manhã? | Was war das Beste am Morgen? | Quel a été le meilleur moment de la matinée ? |
| 7 | What song got stuck in your head? | ¿Qué canción se te ha quedado en la cabeza? | Que música ficou na sua cabeça? | Welches Lied ist dir im Kopf geblieben? | Quelle chanson t'est restée en tête ? |
| 8 | What did you see out the window? | ¿Qué has visto por la ventana? | O que você viu pela janela? | Was hast du aus dem Fenster gesehen? | Qu'as-tu vu par la fenêtre ? |
| 9 | What was the weather like? | ¿Qué tiempo ha hecho? | Que tempo fez? | Wie war das Wetter? | Quel temps a-t-il fait ? |
| 10 | What would you like to remember about today in a year? | ¿Qué te gustaría recordar de hoy dentro de un año? | O que você gostaria de lembrar de hoje daqui a um ano? | Was möchtest du dir von heute in einem Jahr merken? | Que voudrais-tu te rappeler d'aujourd'hui dans un an ? |
| 11 | What did you read today? | ¿Qué has leído hoy? | O que você leu hoje? | Was hast du heute gelesen? | Qu'as-tu lu aujourd'hui ? |
| 12 | What did you make with your hands? | ¿Qué has hecho con las manos? | O que você fez com as mãos? | Was hast du mit den Händen gemacht? | Qu'as-tu fait de tes mains ? |
| 13 | What did you use for the first time today? | ¿Qué has estrenado hoy? | O que você estreou hoje? | Was hast du heute zum ersten Mal benutzt? | Qu'as-tu inauguré aujourd'hui ? |
| 14 | What surprised you? | ¿Qué te ha sorprendido? | O que te surpreendeu? | Was hat dich überrascht? | Qu'est-ce qui t'a surpris ? |
| 15 | What conversation did you enjoy? | ¿Qué conversación te ha gustado? | Que conversa você gostou de ter? | Welches Gespräch hat dir gefallen? | Quelle conversation t'a plu ? |
| 16 | What did you finish today? | ¿Qué has terminado hoy? | O que você terminou hoje? | Was hast du heute fertiggestellt? | Qu'as-tu terminé aujourd'hui ? |
| 17 | What did you start today? | ¿Qué has empezado hoy? | O que você começou hoje? | Was hast du heute angefangen? | Qu'as-tu commencé aujourd'hui ? |
| 18 | What smell do you remember from today? | ¿Qué olor recuerdas de hoy? | Que cheiro você lembra de hoje? | An welchen Geruch erinnerst du dich von heute? | Quelle odeur te rappelles-tu d'aujourd'hui ? |
| 19 | What caught your eye on the street? | ¿Qué te ha llamado la atención por la calle? | O que chamou sua atenção na rua? | Was ist dir auf der Straße aufgefallen? | Qu'est-ce qui a attiré ton attention dans la rue ? |
| 20 | What did you cook, or who cooked for you? | ¿Qué has cocinado, o quién ha cocinado para ti? | O que você cozinhou, ou quem cozinhou para você? | Was hast du gekocht, oder wer hat für dich gekocht? | Qu'as-tu cuisiné, ou qui a cuisiné pour toi ? |
| 21 | What's the plan for tomorrow? | ¿Qué plan tienes para mañana? | Qual é o plano para amanhã? | Was hast du für morgen geplant? | Quel est le plan pour demain ? |
| 22 | What did you buy today? | ¿Qué has comprado hoy? | O que você comprou hoje? | Was hast du heute gekauft? | Qu'as-tu acheté aujourd'hui ? |
| 23 | Where did you walk? | ¿Por dónde has caminado? | Por onde você caminhou? | Wo bist du gelaufen? | Où as-tu marché ? |
| 24 | What went well for you? | ¿Qué te ha salido bien? | O que deu certo para você? | Was ist dir gut gelungen? | Qu'est-ce qui a bien marché pour toi ? |
| 25 | What did you decide today? | ¿Qué has decidido hoy? | O que você decidiu hoje? | Was hast du heute entschieden? | Qu'as-tu décidé aujourd'hui ? |
| 26 | What did you watch on a screen that was worth it? | ¿Qué has visto en una pantalla que valga la pena? | O que você viu numa tela que valeu a pena? | Was hast du auf einem Bildschirm gesehen, das sich gelohnt hat? | Qu'as-tu vu sur un écran qui en valait la peine ? |
| 27 | What message were you glad to receive? | ¿Qué mensaje te ha gustado recibir? | Que mensagem você gostou de receber? | Über welche Nachricht hast du dich gefreut? | Quel message as-tu aimé recevoir ? |
| 28 | What did you do for the first time? | ¿Qué has hecho por primera vez? | O que você fez pela primeira vez? | Was hast du zum ersten Mal gemacht? | Qu'as-tu fait pour la première fois ? |
| 29 | What did you do for someone? | ¿Qué has hecho por alguien? | O que você fez por alguém? | Was hast du für jemanden getan? | Qu'as-tu fait pour quelqu'un ? |
| 30 | What did someone do for you? | ¿Qué ha hecho alguien por ti? | O que alguém fez por você? | Was hat jemand für dich getan? | Qu'est-ce que quelqu'un a fait pour toi ? |
| 31 | What was playing at home? | ¿Qué sonaba en casa? | O que estava tocando em casa? | Was lief bei dir zu Hause? | Qu'est-ce qui passait chez toi ? |
| 32 | What was the calmest moment? | ¿Cuál ha sido el momento más tranquilo? | Qual foi o momento mais tranquilo? | Was war der ruhigste Moment? | Quel a été le moment le plus calme ? |
| 33 | What took longer than you thought? | ¿Qué te ha llevado más tiempo del que pensabas? | O que levou mais tempo do que você pensava? | Was hat länger gedauert, als du dachtest? | Qu'est-ce qui t'a pris plus de temps que prévu ? |
| 34 | What were you looking forward to? | ¿Qué te ha hecho ilusión? | O que te deixou animado? | Worauf hast du dich gefreut? | Qu'est-ce qui t'a fait plaisir ? |
| 35 | What did you find without looking for it? | ¿Qué has encontrado sin buscarlo? | O que você encontrou sem procurar? | Was hast du gefunden, ohne danach zu suchen? | Qu'as-tu trouvé sans le chercher ? |
| 36 | What phrase did you hear today? | ¿Qué frase has oído hoy? | Que frase você ouviu hoje? | Welchen Satz hast du heute gehört? | Quelle phrase as-tu entendue aujourd'hui ? |
| 37 | What did you fix? | ¿Qué has arreglado? | O que você consertou? | Was hast du repariert? | Qu'as-tu réparé ? |
| 38 | What did you see in the sky? | ¿Qué has visto en el cielo? | O que você viu no céu? | Was hast du am Himmel gesehen? | Qu'as-tu vu dans le ciel ? |
| 39 | What time did you get up, and why? | ¿A qué hora te has levantado, y por qué? | A que horas você acordou, e por quê? | Wann bist du aufgestanden, und warum? | À quelle heure t'es-tu levé, et pourquoi ? |
| 40 | What did you celebrate, even something small? | ¿Qué has celebrado, aunque sea algo pequeño? | O que você comemorou, mesmo que pequeno? | Was hast du gefeiert, und sei es etwas Kleines? | Qu'as-tu fêté, même un petit quelque chose ? |
| 41 | What would you carry from today into tomorrow? | ¿Qué te llevarías de hoy a mañana? | O que você levaria de hoje para amanhã? | Was würdest du von heute mit in den morgigen Tag nehmen? | Qu'emporterais-tu d'aujourd'hui vers demain ? |
| 42 | What did you move to a new spot? | ¿Qué has cambiado de sitio? | O que você mudou de lugar? | Was hast du umgestellt? | Qu'as-tu déplacé ? |
| 43 | What did you write today, besides this? | ¿Qué has escrito hoy, aparte de esto? | O que você escreveu hoje, além disso? | Was hast du heute geschrieben, außer diesem hier? | Qu'as-tu écrit aujourd'hui, à part ça ? |
| 44 | What animal did you see? | ¿Qué animal has visto? | Que animal você viu? | Welches Tier hast du gesehen? | Quel animal as-tu vu ? |
| 45 | What flavor did you try for the first time? | ¿Qué sabor has probado por primera vez? | Que sabor você provou pela primeira vez? | Welchen Geschmack hast du zum ersten Mal probiert? | Quelle saveur as-tu goûtée pour la première fois ? |
| 46 | What question were you asked? | ¿Qué pregunta te han hecho? | Que pergunta fizeram para você? | Welche Frage hat man dir gestellt? | Quelle question t'a-t-on posée ? |
| 47 | What kept you busy? | ¿Qué te ha tenido ocupado? | O que te manteve ocupado? | Was hat dich beschäftigt? | Qu'est-ce qui t'a occupé ? |
| 48 | What part of the day went by the fastest? | ¿Qué parte del día se ha pasado más rápido? | Que parte do dia passou mais rápido? | Welcher Teil des Tages ist am schnellsten vergangen? | Quel moment de la journée est passé le plus vite ? |
| 49 | What did you give, or what were you given? | ¿Qué has regalado, o qué te han regalado? | O que você deu de presente, ou o que te deram? | Was hast du verschenkt, oder was hast du geschenkt bekommen? | Qu'as-tu offert, ou qu'est-ce qu'on t'a offert ? |
| 50 | What did you photograph? | ¿Qué has fotografiado? | O que você fotografou? | Was hast du fotografiert? | Qu'as-tu photographié ? |
| 51 | What plan turned out different than expected? | ¿Qué plan ha salido distinto de lo previsto? | Que plano saiu diferente do previsto? | Welcher Plan ist anders gelaufen als gedacht? | Quel plan s'est déroulé différemment que prévu ? |
| 52 | What did you hear on the street? | ¿Qué has oído en la calle? | O que você ouviu na rua? | Was hast du auf der Straße gehört? | Qu'as-tu entendu dans la rue ? |
| 53 | What did you find beautiful? | ¿Qué te ha parecido bonito? | O que você achou bonito? | Was fandest du schön? | Qu'as-tu trouvé beau ? |
| 54 | What news did you talk about? | ¿De qué noticia has hablado? | De que notícia você falou? | Über welche Nachricht hast du gesprochen? | De quelle actualité as-tu parlé ? |
| 55 | What game or sport happened today? | ¿Qué juego o deporte ha habido hoy? | Que jogo ou esporte houve hoje? | Welches Spiel oder welcher Sport war heute? | Quel jeu ou sport y a-t-il eu aujourd'hui ? |
| 56 | What made you feel at home? | ¿Qué te ha hecho sentir en casa? | O que te fez sentir em casa? | Was hat dir das Gefühl gegeben, zu Hause zu sein? | Qu'est-ce qui t'a fait te sentir chez toi ? |
| 57 | What did you leave for another day? | ¿Qué has dejado para otro día? | O que você deixou para outro dia? | Was hast du auf einen anderen Tag verschoben? | Qu'as-tu laissé pour un autre jour ? |
| 58 | What did you see on the way somewhere? | ¿Qué has visto de camino a algún sitio? | O que você viu a caminho de algum lugar? | Was hast du auf dem Weg irgendwohin gesehen? | Qu'as-tu vu en allant quelque part ? |
| 59 | What small detail don't you want to forget? | ¿Qué detalle pequeño no quieres olvidar? | Que detalhe pequeno você não quer esquecer? | Welches kleine Detail möchtest du nicht vergessen? | Quel petit détail ne veux-tu pas oublier ? |
| 60 | How would you sum up today in five words? | ¿Cómo contarías hoy en cinco palabras? | Como você resumiria hoje em cinco palavras? | Wie würdest du den heutigen Tag in fünf Wörtern beschreiben? | Comment résumerais-tu aujourd'hui en cinq mots ? |

---

## 16. v1.2

| Clave | Parámetros | en | es | pt | de | fr |
|---|---|---|---|---|---|---|
| `proRecaps` | | Month and year recaps | Resúmenes del mes y del año | Resumos do mês e do ano | Monats- und Jahresrückblicke | Récapitulatifs du mois et de l'année |
| `proManyPhotos` | | Up to four photos a day | Hasta cuatro fotos por día | Até quatro fotos por dia | Bis zu vier Fotos pro Tag | Jusqu'à quatre photos par jour |
| `recapYearTitle` | year | Your 2027 | Tu 2027 | Seu 2027 | Dein 2027 | Ton 2027 |
| `recapMonthStats` | days, photos | 21 days written, 4 photos | 21 días escritos, 4 fotos | 21 dias escritos, 4 fotos | 21 geschriebene Tage, 4 Fotos | 21 jours écrits, 4 photos |
| `recapLongestStreak` | n | Longest streak: 34 days | Racha más larga: 34 días | Sequência mais longa: 34 dias | Längste Serie: 34 Tage | Plus longue série : 34 jours |
| `moodRow` | | Log your mood | Anotar el ánimo | Anotar o humor | Stimmung eintragen | Noter l'humeur |
| `moodFilter` | | Filter by mood | Filtrar por ánimo | Filtrar por humor | Nach Stimmung filtern | Filtrer par humeur |
| `moodGreat` | | Great | Genial | Ótimo | Super | Génial |
| `moodGood` | | Good | Bien | Bem | Gut | Bien |
| `moodOkay` | | Okay | Normal | Normal | Normal | Normal |
| `moodBad` | | Bad | Mal | Mal | Schlecht | Mauvais |
| `moodAwful` | | Awful | Fatal | Péssimo | Mies | Horrible |
