package com.rchyn.unscramble.ui.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rchyn.unscramble.R
import com.rchyn.unscramble.data.MAX_OF_WORDS
import com.rchyn.unscramble.databinding.DialogScoreBinding
import com.rchyn.unscramble.databinding.FragmentGameBinding
import com.rchyn.unscramble.ui.MainActivity

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null
    private val binding
        get() = _binding as FragmentGameBinding

    private var _bindingDialogScore: DialogScoreBinding? = null
    private val bindingDialogScore
        get() = _bindingDialogScore

    private val viewModel: GameViewModel by viewModels()

    private lateinit var act: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        act = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            textStage.tvTitleText.text = getString(R.string.title_stage)
        }

        binding.textScore.apply {
            tvTitleText.text = getString(R.string.title_score)
            viewModel.score.observe(viewLifecycleOwner) { scorer ->
                tvDescText.text = getString(R.string.score_points, scorer.toString())
            }
        }

        binding.textStage.apply {
            tvTitleText.text = getString(R.string.title_stage)
            viewModel.currentWordCount.observe(viewLifecycleOwner) { count ->
                tvDescText.text =
                    getString(R.string.word_count, count.toString(), MAX_OF_WORDS.toString())

            }
        }

        viewModel.currentScrambleWord.observe(viewLifecycleOwner) { word ->
            binding.tvUnscrambleWord.text = word
        }

        binding.btnSubmit.setOnClickListener {
            onSubmitWord()
        }

        binding.btnSkip.setOnClickListener {
            onSkipWord()
        }
    }

    private fun onSubmitWord() {
        val userWord = binding.edtScrambleWord.text.toString()

        if (viewModel.isUserWordCorrect(userWord)) {
            setErrorTextWord(false)
            binding.edtScrambleWord.setText("")
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextWord(true)
        }
    }

    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextWord(false)
        } else {
            showFinalScoreDialog()
        }
    }

    private fun setErrorTextWord(error: Boolean) {
        if (error) {
            binding.edtLayoutScrambleWord.error = getString(R.string.try_again)
            binding.edtLayoutScrambleWord.isErrorEnabled = true
        } else {
            binding.edtLayoutScrambleWord.error = null
            binding.edtLayoutScrambleWord.isErrorEnabled = false
        }
    }

    private fun showFinalScoreDialog() {
        if (_bindingDialogScore == null) {
            _bindingDialogScore = DialogScoreBinding.inflate(layoutInflater)
            bindingDialogScore?.tvMessage?.text =
                getString(R.string.final_score, viewModel.score.value.toString())
            MaterialAlertDialogBuilder(requireContext()).setView(bindingDialogScore?.root)
                .setCancelable(false).setPositiveButton(getString(R.string.restart)) { _, _ ->
                    restartGame()
                }.setNegativeButton(getString(R.string.exit)) { _, _ ->
                    exitGame()
                }.show()
        }
    }

    private fun restartGame() {
        viewModel.reinitializeUnscramble()
        setErrorTextWord(false)
    }

    private fun exitGame() {
        act.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}