package io.github.sds100.keymapper.mappings.keymaps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.composethemeadapter3.Mdc3Theme
import dagger.hilt.android.AndroidEntryPoint
import io.github.sds100.keymapper.databinding.FragmentComposeViewBinding
import io.github.sds100.keymapper.mappings.ConfigKeyMapNavHost

/**
 * Created by sds100 on 12/07/2022.
 */
@AndroidEntryPoint
class ConfigKeyMapFragment2 : Fragment() {

    private var _binding: FragmentComposeViewBinding? = null
    private val binding: FragmentComposeViewBinding
        get() = _binding!!

    private val args: ConfigKeyMapFragment2Args by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComposeViewBinding.inflate(inflater, container, false)
        binding.composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                Mdc3Theme {
                    ConfigKeyMapNavHost(
                        navigateBack = { findNavController().navigateUp() },
                        keyMapUid = args.keyMapUid
                    )
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}