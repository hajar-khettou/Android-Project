package com.example.ileauxtresors

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ileauxtresors.navigation.Page
import com.example.ileauxtresors.ui.GameViewModel
import com.example.ileauxtresors.ui.pages.AccueilPage
import com.example.ileauxtresors.ui.pages.AttaquePopup
import com.example.ileauxtresors.ui.pages.CapitainePage
import com.example.ileauxtresors.ui.pages.FinPage
import com.example.ileauxtresors.ui.pages.PlateauPage
import com.example.ileauxtresors.ui.pages.ReglesPage
import com.example.ileauxtresors.ui.pages.TavernePage
import com.example.ileauxtresors.ui.theme.IleAuxTresorsTheme

/** La seule activité de l'appli : elle affiche la page courante du GameViewModel. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IleAuxTresorsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PageHost(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/** Associe chaque Page à son écran (l'équivalent de goToPage dans la maquette). */
@Composable
private fun PageHost(modifier: Modifier = Modifier, vm: GameViewModel = viewModel()) {
    val game = vm.game
    when (vm.page) {
        Page.ACCUEIL -> AccueilPage(
            onJouer = { vm.goToPage(Page.CAPITAINE) },
            onRegles = { vm.goToPage(Page.REGLES) },
            modifier = modifier,
        )
        Page.REGLES -> ReglesPage(onRetour = { vm.goToPage(Page.ACCUEIL) }, modifier = modifier)
        Page.CAPITAINE -> CapitainePage(onJouer = vm::startGame, modifier = modifier)
        Page.PLATEAU, Page.ATTAQUE -> if (game != null) {
            PlateauPage(
                game = game,
                onLancer = vm::rollDice,
                onToggleKeep = vm::toggleKeep,
                onValider = vm::validateDice,
                modifier = modifier,
                botLog = vm.botLog,
            )
            val hit = game.islandHit
            if (vm.page == Page.ATTAQUE && hit != null) {
                AttaquePopup(
                    attackerName = game.captains[hit.attackerIndex].name,
                    damage = hit.damage,
                    onRester = { vm.answerAttack(flee = false) },
                    onFuir = { vm.answerAttack(flee = true) },
                )
            }
        }
        Page.TAVERNE -> if (game != null) {
            TavernePage(
                game = game,
                onAcheter = vm::buyCard,
                onBalayer = vm::sweepTavern,
                onFinDuTour = vm::endTurn,
                modifier = modifier,
            )
        }
        Page.FIN -> if (game != null) FinPage(game, onRejouer = vm::replay, onMenu = vm::backToMenu, modifier = modifier)
    }
}
