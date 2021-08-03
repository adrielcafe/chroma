package cafe.adriel.chroma.view.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.chroma.R
import cafe.adriel.chroma.ktx.appVersion
import cafe.adriel.chroma.ktx.isEmail
import cafe.adriel.chroma.ktx.open
import cafe.adriel.chroma.ktx.sendContactEmail
import cafe.adriel.chroma.model.ContactLink
import cafe.adriel.chroma.model.DonationProduct
import cafe.adriel.chroma.view.theme.ChromaColors

private val DONATE_PICKER_WIDTH = 250.dp

@Composable
fun AboutDialog(onClose: () -> Unit) =
    Dialog(onDismissRequest = onClose) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colors.background)
        ) {
            AboutHeader(onClose = onClose)
            Surface(contentColor = MaterialTheme.colors.onBackground) {
                LazyColumn(
                    modifier = Modifier
                        .background(MaterialTheme.colors.background)
                        .padding(top = 8.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
                ) {
                    item {
                        Column {
                            val context = LocalContext.current
                            val appVersion = remember { context.appVersion }

                            Text(
                                text = stringResource(R.string.author_name),
                                color = MaterialTheme.colors.secondary,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.body1,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Text(
                                text = stringResource(R.string.author_job),
                                style = MaterialTheme.typography.body2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                            )
                            Text(
                                text = stringResource(R.string.hi_there_are_you_enjoying_the_app),
                                style = MaterialTheme.typography.caption
                            )
                            ContactButtons()
                            Text(
                                text = stringResource(R.string.this_app_is_open_source),
                                style = MaterialTheme.typography.caption
                            )
                            RepositoryButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Divider(
                                thickness = .5.dp,
                                color = MaterialTheme.colors.primary.copy(alpha = ContentAlpha.medium),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                            )
                            Text(
                                text = appVersion,
                                color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.overline,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
fun DonateDialog(onDonate: (DonationProduct) -> Unit, onClose: () -> Unit) {
    val (selectedProduct, selectProduct) = remember { mutableStateOf(DonationProduct.COFFEE_1) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                text = stringResource(R.string.buy_me_coffee),
                style = MaterialTheme.typography.body1
            )
        },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.app_content_free),
                    style = MaterialTheme.typography.caption
                )
                DonatePicker(
                    selectedProduct = selectedProduct,
                    onSelect = selectProduct
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDonate(selectedProduct)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.secondary),
                modifier = Modifier.height(ButtonDefaults.MinHeight)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.FreeBreakfast,
                        contentDescription = "Donate",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(
                        text = stringResource(R.string.donate).uppercase(),
                        style = MaterialTheme.typography.caption
                    )
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onClose,
                colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onBackground),
                modifier = Modifier.height(ButtonDefaults.MinHeight),
            ) {
                Text(
                    text = stringResource(R.string.later).uppercase(),
                    style = MaterialTheme.typography.caption
                )
            }
        },
        contentColor = MaterialTheme.colors.onBackground,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun AboutHeader(onClose: () -> Unit) =
    ConstraintLayout {
        val (bgRef, authorRef, closeRef) = createRefs()

        Image(
            painter = painterResource(R.drawable.about_bg),
            contentScale = ContentScale.Crop,
            contentDescription = "About background",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .constrainAs(bgRef) {
                    top.linkTo(parent.top)
                }
        )
        Image(
            painter = painterResource(R.drawable.about_author),
            contentScale = ContentScale.Crop,
            contentDescription = "About background",
            modifier = Modifier
                .size(80.dp)
                .border(width = 4.dp, color = MaterialTheme.colors.background, shape = CircleShape)
                .clip(CircleShape)
                .constrainAs(authorRef) {
                    centerHorizontallyTo(parent)
                    top.linkTo(bgRef.bottom, -(40).dp)
                }
        )
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .constrainAs(closeRef) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                tint = MaterialTheme.colors.onBackground,
                contentDescription = "Close"
            )
        }
    }

@Composable
private fun ContactButtons() {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        listOf(ContactLink.WEBSITE, ContactLink.EMAIL, ContactLink.GITHUB_PROFILE, ContactLink.LINKEDIN_PROFILE)
            .map { link ->
                IconButton(
                    onClick = {
                        if (link.url.isEmail()) context.sendContactEmail()
                        else link.url.open(context)
                    }
                ) {
                    Icon(
                        painter = painterResource(link.iconRes),
                        contentDescription = link.name
                    )
                }
            }
    }
}

@Composable
private fun RepositoryButton(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    OutlinedButton(
        onClick = { ContactLink.PROJECT_REPO.url.open(context) },
        colors = ButtonDefaults.buttonColors(contentColor = MaterialTheme.colors.onBackground),
        shape = CircleShape,
        modifier = modifier.padding(top = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(ContactLink.PROJECT_REPO.iconRes),
                contentDescription = "Github logo",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = stringResource(R.string.repository_path),
                softWrap = false,
                style = MaterialTheme.typography.caption
            )
        }
    }
}

@Composable
private fun DonatePicker(selectedProduct: DonationProduct, onSelect: (DonationProduct) -> Unit) =
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(DONATE_PICKER_WIDTH)
            .padding(horizontal = 24.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            DonationProduct.values()
                .map { product ->
                    Text(
                        text = product.label,
                        color = if (product == selectedProduct) MaterialTheme.colors.secondary else ChromaColors.gray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
        }
        Box(
            modifier = Modifier.width(DONATE_PICKER_WIDTH)
        ) {
            val selectedProductOffset by animateDpAsState(selectedProduct.offset)

            Divider(
                thickness = 4.dp,
                color = ChromaColors.gray,
                modifier = Modifier.align(Alignment.Center)
            )
            DonationProduct.values()
                .map { product ->
                    Spacer(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(ChromaColors.gray)
                            .align(product.alignment)
                            .clickable { onSelect(product) }
                    )
                }
            Spacer(
                modifier = Modifier
                    .offset(x = selectedProductOffset)
                    .padding(4.dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colors.secondary)
            )
        }
    }
