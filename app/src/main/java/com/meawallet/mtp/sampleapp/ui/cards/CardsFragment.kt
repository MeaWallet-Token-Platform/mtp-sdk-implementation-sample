package com.meawallet.mtp.sampleapp.ui.cards

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.meawallet.mtp.*
import com.meawallet.mtp.sampleapp.databinding.FragmentCardsBinding


class CardsFragment : Fragment() {

    private var _binding: FragmentCardsBinding? = null

    private lateinit var cardListRv: RecyclerView

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    companion object {
        private val TAG = CardsFragment::class.java.simpleName
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCardsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        cardListRv = binding.cardList

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        cardListRv.layoutManager = layoutManager
        cardListRv.itemAnimator = DefaultItemAnimator()
        cardListRv.adapter = CardListAdapter()

        MeaTokenPlatform.setDigitizedCardStateChangeListener { card, _ ->
            val adapter = cardListRv.adapter as CardListAdapter
            adapter.updateCard(card)
        }

        MeaTokenPlatform.setCardReplenishListener(object : MeaCardReplenishListener {
            override fun onReplenishCompleted(card: MeaCard, numberOfTokens: Int) {
                Log.i(TAG,"onReplenishCompleted(). Card (cardId = ${card.id}) token count: $numberOfTokens Listener object: ${this.hashCode()}")

                val adapter = cardListRv.adapter as CardListAdapter
                adapter.updateCard(card)
            }

            override fun onReplenishFailed(card: MeaCard, error: MeaError) {
                Log.e(TAG,"Card (cardId = ${card.id}) replenish failed: ${error.code} ${error.message}")
            }
        })

        return root
    }

    override fun onStart() {
        super.onStart()

        if (!MeaTokenPlatform.isInitialized() || !MeaTokenPlatform.isRegistered()) {
            return
        }

        val cards = MeaTokenPlatform.getCards()
        if (cards.isNotEmpty()) {
            val adapter = cardListRv.adapter as CardListAdapter
            adapter.updateCards(cards)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}