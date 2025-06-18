package com.example.yap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yap.data.CardDatabase
import com.example.yap.data.SetEntity
import com.example.yap.data.cardEntity
import com.example.yap.ui.theme.CardViewModel
import com.example.yap.ui.theme.Repository
import com.example.yap.ui.theme.ViewModelFactory
import com.example.yap.ui.theme.YAPTheme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
import kotlin.system.exitProcess


class MainActivity : ComponentActivity() {
    private lateinit var cardViewModel: CardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val dao = CardDatabase.getDatabase(this).cardDao()
            val repository = Repository(dao)
            val factory = ViewModelFactory(repository)
            cardViewModel = ViewModelProvider(this, factory)[CardViewModel::class.java]
            YAPTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    MainNavigation(cardViewModel)
                }

            }
        }
    }
}
@Composable
fun MainNavigation(cardViewModel: CardViewModel)
{
    val navController = rememberNavController()
    NavHost(navController = navController,startDestination = "Home" )
    {
        composable("Home"){ Home(navController = navController,cardViewModel) }
        composable("AddDeckScreen"){ AddSetScreen(navController = navController,cardViewModel = cardViewModel) }

        composable("AddCardsScreen/{setId}/{frmCrdScr}", arguments = listOf(navArgument("setId"){type = NavType.LongType}, navArgument("frmCrdScr"){type = NavType.BoolType})) { backStackEntry ->
            val setId = backStackEntry.arguments?.getLong("setId")
            val fromCrdScr = backStackEntry.arguments?.getBoolean("frmCrdScr") ?: false
            if (setId != null) {
                AddCardScreen(navController, cardViewModel, setId,fromCrdScr)
            }
        }


        composable("CardScreen/{setId}", arguments = listOf(navArgument("setId"){type = NavType.LongType}))
        {   backStackEntry-> val setId = backStackEntry.arguments?.getLong("setId") ?: return@composable
            CardScreen(cardViewModel = cardViewModel, navController = navController, setId = setId)}

        composable("PracticeScreen/{setId}", arguments = listOf(navArgument("setId"){type = NavType.LongType})) {
            val setId = it.arguments?.getLong("setId") ?: return@composable
            PracticeScreen(navController, cardViewModel, setId)
        }
        composable("AboutAppScreen") { AboutAppScreen(navController)  }

    }
}


@Composable
fun AddCardScreen(navController: NavController,cardViewModel: CardViewModel, setId: Long, fromCrdScr: Boolean)
{
    val question = remember{mutableStateOf("")}
    val ans = remember{mutableStateOf("")}
    val frmCrdScr = remember { fromCrdScr }



    Surface( Modifier .fillMaxSize())
    {
        Column(verticalArrangement = Arrangement.Center, modifier = Modifier .padding(horizontal = 8.dp))
        {
            OutlinedTextField(
                value = question.value,
                onValueChange = {question.value = it},
                label = {Text("Enter question")},
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth() .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.padding(vertical = 8.dp))
            OutlinedTextField(
                value = ans.value,
                onValueChange = {ans.value = it},
                label = {Text("Enter answer")},
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth() .align(Alignment.CenterHorizontally)
            )
            Button(onClick =
            {
                if(question.value.isNotBlank() && ans.value.isNotBlank())
                {

                    cardViewModel.currentSetId = setId
                    cardViewModel.addCard(cardEntity(front = question.value,back = ans.value,setId =setId))
                    Log.d("AddCard", "Attempting to insert card with setId: $setId")
                    navController.navigate("AddCardsScreen/$setId/$frmCrdScr")
                }
            },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier .padding(vertical = 10.dp) .align(Alignment.End) .fillMaxWidth())
            {Text("Add another card", style = MaterialTheme.typography.labelLarge,color =if(isSystemInDarkTheme()) Color.White else Color.Black )}
            Button(
                onClick = {
                    if(!frmCrdScr)
                    navController.navigate("Home")
                    else
                        navController.navigate("CardScreen/$setId")
                }
            )
            {Text("Go back",color = if(isSystemInDarkTheme()) Color.White else Color.Black)}
        }

    }
}


@Composable
fun AddSetScreen(navController: NavController, cardViewModel: CardViewModel) {
    val name = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    Surface(Modifier .fillMaxSize())
    {

        Column(
            modifier = Modifier.padding(vertical = 50.dp, horizontal = 10.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.Top
        )
        { Box()
            {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.TopStart).padding(top = 16.dp)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
            OutlinedTextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Enter set name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(vertical = 10.dp))
            OutlinedTextField(
                value = description.value,
                onValueChange = { description.value = it },
                label = { Text("Enter set description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.padding(vertical = 10.dp))
            Button(onClick =
            {
                if (name.value.isNotBlank()) {
                    coroutineScope.launch {
                        val id = cardViewModel.addSet(
                            SetEntity(
                                name = name.value,
                                description = description.value
                            )
                        )
                        Log.d("AddCard", "Attempting to insert Set with setId: $id")
                        navController.navigate("AddCardsScreen/$id/${false}")
                        name.value = ""
                        description.value = ""

                    }
                }
            }, modifier = Modifier.align(Alignment.End))
            {
                Text("Add Cards", style = MaterialTheme.typography.labelLarge,color = if (isSystemInDarkTheme()) Color.White else Color.Black)
            }
        }
    }
}

@Composable
fun FlashDeckList(modifier: Modifier = Modifier, cardViewModel: CardViewModel, navController: NavController)
{
    val sets by cardViewModel.allSets.collectAsState(initial = emptyList())
    LazyColumn{
        items(sets)
        {
            set-> FlashDeckCard(modifier = modifier.clickable{ navController.navigate("CardScreen/${set.id}") },setEntity = set, navController = navController, cardViewModel = cardViewModel )
        }
    }
}



@Composable
fun FlashDeckCard(modifier: Modifier = Modifier, setEntity: SetEntity, navController: NavController, cardViewModel: CardViewModel) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    // For the edit dialog
    var newName by remember { mutableStateOf(setEntity.name) }
    var newDescription by remember { mutableStateOf(setEntity.description) }

    Box(Modifier.padding(8.dp)) {
        ElevatedCard(
            modifier = modifier.fillMaxWidth().padding(2.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(Modifier.padding(8.dp)) {
                Text(
                    setEntity.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.padding(vertical = 5.dp))
                Text(
                    setEntity.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontWeight = FontWeight.W300
                )
                Spacer(Modifier.padding(vertical = 10.dp))
                Row(horizontalArrangement = Arrangement.Start) {
                    Button(onClick = { navController.navigate("PracticeScreen/${setEntity.id}") }, shape = RoundedCornerShape(20f)) {
                        Text(text = "Practice", color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    }
                    Spacer(Modifier.width(10.dp))
                    Button(onClick = { showEditDialog = true }, shape = RoundedCornerShape(20f)) {
                        Text(text = "Edit", color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                    }
                }
            }
        }

        IconButton(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(text = "Confirm Deletion", color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary)
            },
            text = {
                Text("Are you sure you want to delete this set? This action cannot be undone.")
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    cardViewModel.deleteSet(setEntity)
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Dialog
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = {
                Text(text = "Edit Set", color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary)
            },
            text = {
                Column {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Set Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newDescription,
                        onValueChange = { newDescription = it },
                        label = { Text("Set Description") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // Update the setEntity with new values
                    showEditDialog = false
                    cardViewModel.EditSet(setEntity.copy(name = newName, description = newDescription))
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardScreen(setId: Long, cardViewModel: CardViewModel, navController: NavController) {
    LaunchedEffect(setId) {
        cardViewModel.getCardsForSet(setId)
        cardViewModel.getSetName(setId)
        cardViewModel.getSet(setId)
    }

    val cards by cardViewModel.cards.collectAsState(emptyList())
    val setName by cardViewModel.setName
    val set by cardViewModel.setEntity

    var selectedCard by remember { mutableStateOf<cardEntity?>(null) }
    var isDialogVisible by remember { mutableStateOf(false) }

    // Confirmation Dialog States
    var isSetDeleteDialogVisible by remember { mutableStateOf(false) }
    var isCardDeleteDialogVisible by remember { mutableStateOf(false) }

    fun openEditDialog(card: cardEntity) {
        selectedCard = card
        isDialogVisible = true
    }

    fun saveChanges(front: String, back: String) {
        selectedCard?.let { card ->
            val updatedCard = card.copy(front = front, back = back)
            cardViewModel.editCard(updatedCard)
            isDialogVisible = false
        }
    }

    fun dismissDialog() {
        isDialogVisible = false
        selectedCard = null
    }

    // Delete confirmation logic
    fun confirmDeleteSet() {
        set?.let {
            cardViewModel.deleteSet(it)
            navController.navigate("Home")
        }
        isSetDeleteDialogVisible = false
    }

    fun confirmDeleteCard(card: cardEntity) {
        cardViewModel.deleteCard(card)
        isCardDeleteDialogVisible = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = setName ?: "loading..",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("Home") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSetDeleteDialogVisible = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Set",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddCardsScreen/$setId/${true}") },
                shape = RoundedCornerShape(13.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = "Add deck"
                )
            }
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(cards) { card ->
                FlipCard(
                    cardEntity = card,
                    onDelete = { card ->
                        isCardDeleteDialogVisible = true
                        selectedCard = card // Store selected card for deletion
                    },
                    onEdit = { cardToEdit ->
                        openEditDialog(cardToEdit)
                    }
                )
            }
        }

        // Show the Edit Dialog when it's visible
        if (isDialogVisible && selectedCard != null) {
            EditCardDialog(
                card = selectedCard!!,
                onDismiss = { dismissDialog() },
                onSave = { front, back -> saveChanges(front, back) }
            )
        }

        // Show the Set Deletion Confirmation Dialog
        if (isSetDeleteDialogVisible) {
            AlertDialog(
                onDismissRequest = { isSetDeleteDialogVisible = false },
                title = {
                    Text(text = "Confirm Deletion", color = MaterialTheme.colorScheme.onSurface)
                },
                text = {
                    Text("Are you sure you want to delete this set? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = { confirmDeleteSet() }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isSetDeleteDialogVisible = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Show the Card Deletion Confirmation Dialog
        if (isCardDeleteDialogVisible && selectedCard != null) {
            AlertDialog(
                onDismissRequest = { isCardDeleteDialogVisible = false },
                title = {
                    Text(text = "Confirm Card Deletion", color = MaterialTheme.colorScheme.onSurface)
                },
                text = {
                    Text("Are you sure you want to delete this card? This action cannot be undone.")
                },
                confirmButton = {
                    TextButton(onClick = { confirmDeleteCard(selectedCard!!) }) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isCardDeleteDialogVisible = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}



@Composable
fun EditCardDialog(
    card: cardEntity,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var frontText by remember { mutableStateOf(card.front) }
    var backText by remember { mutableStateOf(card.back) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isDarkMode = isSystemInDarkTheme()

    val textColor = if (isDarkMode) Color.White else Color.Black
    val buttonColor = if (isDarkMode) Color.White else Color.Black
    val backgroundColor = if (isDarkMode) Color.Black else Color.White

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Edit Card",
                color = textColor
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = frontText,
                    onValueChange = { frontText = it },
                    label = { Text("Front Text", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(textColor),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = backText,
                    onValueChange = { backText = it },
                    label = { Text("Back Text", color = textColor) },
                    colors = OutlinedTextFieldDefaults.colors(textColor),
                    modifier = Modifier.fillMaxWidth()
                )

                // Display error message if any field is empty
                errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (frontText.isBlank() || backText.isBlank()) {
                        errorMessage = "Both Front and Back texts must be filled"
                    } else {
                        errorMessage = null // Clear the error message
                        onSave(frontText, backText)
                        onDismiss()
                    }
                }
            ) {
                Text("Save", color = buttonColor)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel", color = buttonColor)
            }
        },
        containerColor = backgroundColor
    )
}

@Composable
fun FlipCard(cardEntity: cardEntity, onDelete: (cardEntity) -> Unit, onEdit: (cardEntity) -> Unit) {
    var isFlipped by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        isFlipped = !isFlipped // Flip the card on tap
                    }
                )
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if(isFlipped)MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.surface ),
            contentAlignment = Alignment.Center
        ) {
            val displayText = if (isFlipped) cardEntity.back else cardEntity.front
            val textStyle = if (isFlipped) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium

            Text(
                text = displayText,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                style = textStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )

            // Delete Button
            IconButton(
                onClick = { onDelete(cardEntity) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }

            // Edit Button
            IconButton(
                onClick = { onEdit(cardEntity) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(navController: NavController, cardViewModel: CardViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "FlashCards",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_action_name),
                        contentDescription = "App Logo",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .size(32.dp)
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("AboutAppScreen")}) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About App",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { exitProcess(0) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Exit App",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddDeckScreen") },
                shape = RoundedCornerShape(13.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add deck"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxSize()
        ) {
            FlashDeckList(cardViewModel = cardViewModel, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(navController: NavController, cardViewModel: CardViewModel, setId: Long) {
    LaunchedEffect(setId) {
        cardViewModel.getCardsForSet(setId)
    }
    val cards by cardViewModel.cards.collectAsState(emptyList())


    if (cards.isEmpty()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Practice",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No cards available!", color = MaterialTheme.colorScheme.onBackground)
            }
        }
        return
    }

    var currentCard  by remember{ mutableStateOf(cardViewModel.pickCardWeighted(cards))}
    val offsetX = remember { Animatable(0f) }
    val swipeThreshold = 300f
    val coroutineScope = rememberCoroutineScope()
    var screenWidth by remember { mutableIntStateOf(0) }
    var isFlipped by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Practice",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .onSizeChanged { screenWidth = it.width }
                .background(
                    brush = when {
                        offsetX.value < -swipeThreshold / 2 -> Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE58F8F), Color.Transparent),
                            startX = 0f, endX = swipeThreshold * 0.2f
                        )
                        offsetX.value > swipeThreshold / 2 -> Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, Color(0xFF94CB7E)),
                            startX = screenWidth * 0.95f, endX = screenWidth.toFloat()
                        )
                        else -> Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    coroutineScope.launch {
                                        if (offsetX.value > swipeThreshold || offsetX.value < -swipeThreshold) {
                                            currentCard = cardViewModel.pickCardWeighted(cards)
                                            isFlipped = false
                                        }
                                        offsetX.animateTo(0f)
                                    }
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        offsetX.snapTo(offsetX.value + dragAmount.x)
                                    }
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectTapGestures {
                                isFlipped = !isFlipped
                            }
                        }
                        .graphicsLayer {
                            rotationY = if (isFlipped) 180f else 0f
                            cameraDistance = 12 * density
                            alpha = 1f - (offsetX.value.absoluteValue / swipeThreshold)
                        },
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Box(Modifier.fillMaxSize() .background(if(isFlipped)MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                        AnimatedContent(
                            targetState = isFlipped,
                            transitionSpec = {
                                (fadeIn() + scaleIn()) togetherWith (fadeOut() + scaleOut())
                            },
                            label = "Flip"
                        ) { flipped ->
                            Text(
                                text = if (!flipped) currentCard!!.front else currentCard!!.back,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier
                                    .padding(24.dp)
                                    .graphicsLayer {
                                        rotationY = if (isFlipped) 180f else 0f
                                    },
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutAppScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About FlashCards",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_action_name),
                contentDescription = "App Logo",
                modifier = Modifier.size(100.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "FlashCards\nVersion 1.02",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Created For BCA 4th semester(Minor project)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Name: Amit Rai\n" +
                        "Roll: 23992257\n\n",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}




